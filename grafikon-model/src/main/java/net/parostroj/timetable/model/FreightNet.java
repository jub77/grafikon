package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import net.parostroj.timetable.model.FreightConnectionFilter.FilterContext;
import net.parostroj.timetable.model.FreightConnectionFilter.FilterResult;
import net.parostroj.timetable.model.events.AttributesListener;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.ListData;
import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.Observable;
import net.parostroj.timetable.model.freight.FreightFactory;
import net.parostroj.timetable.model.freight.FreightConnectionPath;
import net.parostroj.timetable.model.freight.NodeConnectionEdges;
import net.parostroj.timetable.model.freight.NodeConnectionNodes;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Managed freight data. Id is shared with train diagram.
 *
 * @author jub
 */
public class FreightNet implements Visitable, ObjectWithId, AttributesHolder, Observable, TrainDiagramPart {

    private final String id;
    private final TrainDiagram diagram;
    private final Attributes attributes;
    private final AttributesListener defaultAttributesListener;
    private final ListenerSupport listenerSupport;

    private final ListMultimap<TimeInterval, FNConnection> fromMap = ArrayListMultimap.create();
    private final ListMultimap<TimeInterval, FNConnection> toMap = ArrayListMultimap.create();
    private final Multimap<Train, FNConnection> fromTrainMap = HashMultimap.create();
    private final Multimap<Train, FNConnection> toTrainMap = HashMultimap.create();

    private final FreightRegionGraphDelegate regionConnections;

    FreightNet(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.regionConnections = new FreightRegionGraphDelegate(diagram);
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
        List<FNConnection> toBeDeleted = new ArrayList<>();
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
    public TrainDiagram getDiagram() {
        return diagram;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Attributes getAttributes() {
        return this.attributes;
    }

    public Map<Train, List<FreightConnectionPath>> getFreightPassedInNode(TimeInterval fromInterval) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        Map<Train, List<FreightConnectionPath>> result = new LinkedHashMap<>();
        List<FNConnection> connections = this.getTrainsFrom(fromInterval);
        for (FNConnection conn : connections) {
            List<FreightConnectionPath> nodes = this.getFreightToNodesImpl(conn.getTo(), conn.getFreightDstFilter(FreightConnectionFilterFactory.createEmptyFilter(), true));
            result.put(conn.getTo().getTrain(), nodes);
        }
        return result;
    }

    public List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        return this.getFreightToNodesImpl(fromInterval, FreightConnectionFilterFactory.createEmptyFilter());
    }

    private List<FreightConnectionPath> getFreightToNodesImpl(TimeInterval fromInterval, FreightConnectionFilter filter) {
        List<FreightConnectionPath> result = new LinkedList<>();
        this.getFreightToNodesImpl(fromInterval.getOwnerAsNode(), fromInterval, Collections.<TimeInterval>emptyList(), result, new HashSet<FNConnection>(), filter, new FilterContext(fromInterval));
        return result;
    }

    private void getFreightToNodesImpl(Node start, TimeInterval fromInterval, List<TimeInterval> path, List<FreightConnectionPath> result, Set<FNConnection> used, FreightConnectionFilter filter, FilterContext context) {
        FilterResult filterResult = FilterResult.OK;
        Iterator<TimeInterval> intervals = fromInterval.getTrain().iterator();
        Iterators.find(intervals, interval -> interval == fromInterval);
        intervals = Iterators.filter(intervals,
                interval -> interval.isNodeOwner() && (interval.isFreightTo() || interval.isFreightConnection()));
        while (intervals.hasNext()) {
            TimeInterval i = intervals.next();
            if (i.isFreight()) {
                FreightConnectionPath newDst = FreightFactory.createFreightNodeConnection(start, i.getOwnerAsNode(), i, path);
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
                    List<TimeInterval> newPath = new ArrayList<>(path.size() + 1);
                    newPath.addAll(path);
                    newPath.add(conn.getFrom());
                    this.getFreightToNodesImpl(start, conn.getTo(), newPath, result, used, conn.getFreightDstFilter(filter, false), context);
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

    public Collection<NodeConnectionNodes> getRegionConnectionNodes() {
        return regionConnections.getRegionConnectionNodes();
    }

    public Collection<NodeConnectionEdges> getRegionConnectionEdges() {
        return regionConnections.getRegionConnectionEdges();
    }

    @Override
    public String toString() {
        return String.format("FreightNet[connections=%d]", fromMap.size());
    }
}
