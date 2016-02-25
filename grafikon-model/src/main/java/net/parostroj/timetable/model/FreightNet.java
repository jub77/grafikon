package net.parostroj.timetable.model;

import java.util.*;

import com.google.common.base.Predicate;
import com.google.common.collect.*;

import net.parostroj.timetable.model.FreightDstFilter.FilterContext;
import net.parostroj.timetable.model.FreightDstFilter.FilterResult;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Managed freight data. Id is shared with train diagram.
 *
 * @author jub
 */
public class FreightNet implements Visitable, ObjectWithId, AttributesHolder, Observable {

    private final String id;
    private final Attributes attributes;
    private final AttributesListener defaultAttributesListener;
    private final ListenerSupport listenerSupport;

    private final ListMultimap<TimeInterval, FNConnection> fromMap = ArrayListMultimap.create();
    private final ListMultimap<TimeInterval, FNConnection> toMap = ArrayListMultimap.create();
    private final Multimap<Train, FNConnection> fromTrainMap = HashMultimap.create();
    private final Multimap<Train, FNConnection> toTrainMap = HashMultimap.create();

    private FreightConverter converter;

    public FreightNet(String id) {
        this.id = id;
        this.converter = new FreightConverter();
        this.listenerSupport = new ListenerSupport();
        this.defaultAttributesListener = (attributes, change) -> {
            Event event = null;
            if (attributes instanceof FNConnection) {
                event = new Event(FreightNet.this, attributes, change);
            } else {
                event = new Event(FreightNet.this, change);
            }
            listenerSupport.fireEvent(event);
        };
        this.attributes = new Attributes(this.defaultAttributesListener);
    }

    public FNConnection addConnection(TimeInterval from, TimeInterval to) {
        if (from == to || from.getOwnerAsNode() != to.getOwnerAsNode()) {
            throw new IllegalArgumentException(String.format("Invalid connection: %s -> %s", from, to));
        }
        FNConnection conn = new FNConnection(from, to, this.defaultAttributesListener);
        this.addConnectionImpl(conn);
        return conn;
    }

    public void removeConnection(FNConnection conn) {
        this.removeConnectionImpl(conn);
    }

    public void moveConnection(FNConnection conn, int index) {
        int currentIndex = fromMap.get(conn.getFrom()).indexOf(conn);
        if (currentIndex != -1) {
            this.moveConnectionImpl(conn, currentIndex, index);
        }
    }

    public Collection<FNConnection> getConnections() {
        return Collections.unmodifiableCollection(fromMap.values());
    }

    public FNConnection getConnection(TimeInterval from, TimeInterval to) {
    	List<FNConnection> connectionList = fromMap.get(from);
        for (FNConnection i : connectionList) {
            if (i.getTo() == to) {
                return i;
            }
        }
        return null;
    }

    private void addConnectionImpl(FNConnection conn) {
        this.fromMap.put(conn.getFrom(), conn);
        this.toMap.put(conn.getTo(), conn);
        this.fromTrainMap.put(conn.getFrom().getTrain(), conn);
        this.toTrainMap.put(conn.getTo().getTrain(), conn);
        this.fireEvent(new Event(this, Event.Type.ADDED, conn));
    }

    private void removeConnectionImpl(FNConnection conn) {
        boolean removed = this.fromMap.remove(conn.getFrom(), conn);
        if (removed) {
            this.toMap.remove(conn.getTo(), conn);
            this.fromTrainMap.remove(conn.getFrom().getTrain(), conn);
            this.toTrainMap.remove(conn.getTo().getTrain(), conn);
            this.fireEvent(new Event(this, Event.Type.REMOVED, conn));
        }
    }

    private void moveConnectionImpl(FNConnection conn, int indexFrom, int indexTo) {
        List<FNConnection> connList = this.fromMap.get(conn.getFrom());
        if (indexTo < 0 || indexTo >= connList.size()) {
            throw new IllegalArgumentException(String.format("Connection moved to illegal place: %d (size %d)", indexTo, connList.size()));
        }
        FNConnection removedConn = connList.remove(indexFrom);
        if (removedConn != null) {
            connList.add(indexTo, conn);
            this.fireEvent(new Event(this, Event.Type.MOVED, conn, ListData.createData(indexFrom, indexTo)));
        }
    }

    public void checkTrain(Train train) {
        Collection<FNConnection> connections = fromTrainMap.get(train);
        List<FNConnection> toBeDeleted = new ArrayList<FNConnection>();
        for (FNConnection conn : connections) {
            TimeInterval fromInterval = conn.getFrom();
            if (!fromInterval.getTrain().isManagedFreight() || !fromInterval.isStop()) {
                toBeDeleted.add(conn);
            }
        }
        connections = toTrainMap.get(train);
        for (FNConnection conn : connections) {
            TimeInterval toInterval = conn.getTo();
            if (!toInterval.getTrain().isManagedFreight() || !toInterval.isStop()) {
                toBeDeleted.add(conn);
            }
        }
        for (FNConnection conn : toBeDeleted) {
            this.removeConnection(conn);
        }
    }

    public void removeTrain(Train train) {
        Set<FNConnection> toBeDeleted = new HashSet<>();
        toBeDeleted.addAll(fromTrainMap.get(train));
        toBeDeleted.addAll(toTrainMap.get(train));
        for (FNConnection conn : toBeDeleted) {
            this.removeConnection(conn);
        }
    }

    @Override
    public void addListener(Listener listener) {
        listenerSupport.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listenerSupport.removeListener(listener);
    }


    private void fireEvent(Event event) {
        this.listenerSupport.fireEvent(event);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Attributes getAttributes() {
        return this.attributes;
    }

    public FreightConverter getConverter() {
		return converter;
	}

    public Map<Train, List<FreightDst>> getFreightPassedInNode(TimeInterval fromInterval) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        Map<Train, List<FreightDst>> result = new LinkedHashMap<Train, List<FreightDst>>();
        List<FNConnection> connections = this.getTrainsFrom(fromInterval);
        for (FNConnection conn : connections) {
            List<FreightDst> nodes = this.getFreightToNodesImpl(conn.getTo(), conn.getFreightDstFilter(FreightDstFilterFactory.createEmptyFilter(), true));
            result.put(conn.getTo().getTrain(), nodes);
        }
        return result;
    }

    public List<FreightDst> getFreightToNodes(TimeInterval fromInterval) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        return this.getFreightToNodesImpl(fromInterval, FreightDstFilterFactory.createEmptyFilter());
    }

    private List<FreightDst> getFreightToNodesImpl(TimeInterval fromInterval, FreightDstFilter filter) {
        List<FreightDst> result = new LinkedList<FreightDst>();
        this.getFreightToNodesImpl(fromInterval, Collections.<TimeInterval>emptyList(), result, new HashSet<FNConnection>(), filter, new FilterContext(fromInterval));
        return result;
    }

    private void getFreightToNodesImpl(TimeInterval fromInterval, List<TimeInterval> path, List<FreightDst> result, Set<FNConnection> used, FreightDstFilter filter, FilterContext context) {
        FilterResult filterResult = FilterResult.OK;
        Iterable<TimeInterval> freightTimeIntervals = Iterables.filter(
                fromInterval.getTrain().getNodeIntervals(),
                new AdvanceFilterFreightOrConnection(fromInterval));
        boolean regionsTransitive = !fromInterval.getTrain().isNoTransitiveCenterOfRegion();
        boolean regionTransfer = fromInterval.getTrain().isRegionTransfer();
        for (TimeInterval i : freightTimeIntervals) {
            if (i.isFreight()) {
                FreightDst newDst = new FreightDst(i.getOwnerAsNode(), i.getTrain(), path);
                filterResult = filter.accepted(context, newDst, 0);
                if (filterResult == FilterResult.STOP_EXCLUDE) {
                    break;
                }
                if (filterResult != FilterResult.IGNORE) {
                    result.add(newDst);
                }
                if (filterResult == FilterResult.STOP_INCLUDE) {
                    break;
                }
            }
            for (FNConnection conn : this.getTrainsFrom(i)) {
                if (!used.contains(conn)) {
                    used.add(conn);
                    List<TimeInterval> newPath = new ArrayList<TimeInterval>(path.size() + 1);
                    newPath.addAll(path);
                    newPath.add(conn.getFrom());
                    this.getFreightToNodesImpl(conn.getTo(), newPath, result, used, conn.getFreightDstFilter(filter, false), context);
                }
            }
            Node node = i.getOwnerAsNode();
            if (!regionTransfer && regionsTransitive && !node.getCenterRegions().isEmpty()) {
                for (TimeInterval interval : node) {
                    Train train = interval.getTrain();
                    if (node == train.getStartNode() && train.isRegionTransfer()) {
                        for (Region region : train.getEndNode().getCenterRegions()) {
                            FreightDst regionDst = new FreightDst(region, null);
                            filterResult = filter.accepted(context, regionDst, 1);
                            if (filterResult == FilterResult.OK || filterResult == FilterResult.STOP_INCLUDE) {
                                result.add(regionDst);
                            }
                        }
                    }
                }
            }
        }
    }

    public List<FNConnection> getTrainsFrom(TimeInterval fromInterval) {
    	return fromMap.get(fromInterval);
    }

    public List<FNConnection> getTrainsTo(TimeInterval toInterval) {
    	return toMap.get(toInterval);
    }

    @Override
    public String toString() {
        return String.format("FreightNet[connections=%d]", fromMap.size());
    }

    private static class AdvanceFilterFreightOrConnection implements Predicate<TimeInterval> {

        private final TimeInterval lastSkipped;
        private boolean skip = true;

        public AdvanceFilterFreightOrConnection(TimeInterval lastSkipped) {
            this.lastSkipped = lastSkipped;
        }

        @Override
        public boolean apply(TimeInterval interval) {
            if (skip) {
                skip = interval != lastSkipped;
                return false;
            } else {
                return interval.isFreightTo() || interval.isFreightConnection();
            }
        }
    }
}
