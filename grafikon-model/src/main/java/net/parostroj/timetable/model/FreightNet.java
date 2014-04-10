package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.actions.FreightHelper;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Managed freight data. Id is shared with train diagram.
 *
 * @author jub
 */
public class FreightNet implements Visitable, ObjectWithId, AttributesHolder {

    private final String id;
    private Attributes attributes;
    private final AttributesListener attributesListener;
    private final GTListenerSupport<FreightNetListener, FreightNetEvent> listenerSupport;

    private final Map<Train, List<FNConnection>> fromMap = new HashMap<Train, List<FNConnection>>();
    private final Map<Train, List<FNConnection>> toMap = new HashMap<Train, List<FNConnection>>();
    private final Set<FNConnection> connections = new HashSet<FNConnection>();

    public FreightNet(String id) {
        this.id = id;
        this.attributesListener = new AttributesListener() {
            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                FreightNetEvent event = null;
                if (attributes instanceof FNConnection) {
                    event = new FreightNetEvent(FreightNet.this, GTEventType.FREIGHT_NET_CONNECTION_ATTRIBUTE, change, (FNConnection) attributes);
                } else {
                    event = new FreightNetEvent(FreightNet.this, change);
                }
                listenerSupport.fireEvent(event);
            }
        };
        this.listenerSupport = new GTListenerSupport<FreightNetListener, FreightNetEvent>(
                new GTEventSender<FreightNetListener, FreightNetEvent>() {
                    @Override
                    public void fireEvent(FreightNetListener listener, FreightNetEvent event) {
                        listener.freightNetChanged(event);
                    }
                });
        this.setAttributes(new Attributes());
    }

    public FNConnection addConnection(TimeInterval from, TimeInterval to) {
        FNConnection conn = new FNConnection(from, to, attributesListener);
        this.addConnectionImpl(conn);
        return conn;
    }

    public void removeConnection(FNConnection conn) {
        this.removeConnectionImpl(conn);
    }

    public Collection<FNConnection> getConnections() {
        return Collections.unmodifiableCollection(connections);
    }

    private void addConnectionImpl(FNConnection conn) {
        connections.add(conn);
        this.addConn(fromMap, conn, conn.getFrom().getTrain());
        this.addConn(toMap, conn, conn.getTo().getTrain());
        this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_CONNECTION_ADDED, null, conn));
    }

    private void removeConnectionImpl(FNConnection conn) {
        boolean removed = connections.remove(conn);
        if (removed) {
            this.removeConn(fromMap, conn, conn.getFrom().getTrain());
            this.removeConn(toMap, conn, conn.getTo().getTrain());
            this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_CONNECTION_REMOVED, null, conn));
        }
    }

    private void addConn(Map<Train, List<FNConnection>> map, FNConnection conn, Train train) {
        if (!map.containsKey(train)) {
            map.put(train, new ArrayList<FNConnection>());
        }
        map.get(train).add(conn);
    }

    private void removeConn(Map<Train, List<FNConnection>> map, FNConnection conn, Train train) {
        if (map.containsKey(train)) {
            map.get(train).remove(conn);
        }
    }

    public void checkTrain(Train train) {
        Collection<FNConnection> connections = this.get(train, fromMap);
        List<FNConnection> toBeDeleted = new ArrayList<FNConnection>();
        for (FNConnection conn : connections) {
            if (!FreightHelper.isFreight(conn.getFrom())) {
                toBeDeleted.add(conn);
            }
        }
        connections = this.get(train, toMap);
        for (FNConnection conn : connections) {
            if (!FreightHelper.isFreight(conn.getTo())) {
                toBeDeleted.add(conn);
            }
        }
        for (FNConnection conn : toBeDeleted) {
            this.removeConnection(conn);
        }
    }

    public void addListener(FreightNetListener listener) {
        listenerSupport.addListener(listener);
    }

    public void removeListener(FreightNetListener listener) {
        listenerSupport.removeListener(listener);
    }


    private void fireEvent(FreightNetEvent event) {
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
    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        this.attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return this.attributes.get(key);
    }

    @Override
    public Attributes getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Attributes attributes) {
        if (this.attributes != null) {
            this.attributes.removeListener(attributesListener);
        }
        this.attributes = attributes;
        this.attributes.addListener(attributesListener);
    }

    public Map<Train, List<FreightDst>> getFreightPassedInNode(TimeInterval fromInterval) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        Map<Train, List<FreightDst>> result = new HashMap<Train, List<FreightDst>>();
        List<FNConnection> connections = this.getTrainsFrom(fromInterval);
        for (FNConnection conn : connections) {
            List<FreightDst> nodes = this.getFreightToNodes(conn.getTo());
            result.put(conn.getTo().getTrain(), nodes);
        }
        return result;
    }

    public List<FreightDst> getFreightToNodes(TimeInterval fromInterval) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        List<FreightDst> result = new LinkedList<FreightDst>();
        this.getFreightToNodesImpl(fromInterval, result, new HashSet<FNConnection>());
        return result;
    }

    private void getFreightToNodesImpl(TimeInterval fromInterval, List<FreightDst> result, Set<FNConnection> used) {
        List<FNConnection> nextConns = getNextTrains(fromInterval);
        for (TimeInterval i : FreightHelper.getNodeIntervalsWithFreight(fromInterval.getTrain().getTimeIntervalList(), fromInterval)) {
            result.add(new FreightDst(i.getOwnerAsNode(), i.getTrain()));
            for (FNConnection conn : nextConns) {
                if (i == conn.getFrom() && !used.contains(conn)) {
                    used.add(conn);
                    this.getFreightToNodesImpl(conn.getTo(), result, used);
                }
            }
        }
    }

    public List<FNConnection> getNextTrains(TimeInterval fromInterval) {
        List<FNConnection> result = new LinkedList<FNConnection>();
        int index = fromInterval.getTrain().getIndexOfInterval(fromInterval);
        Collection<FNConnection> connections = this.get(fromInterval.getTrain(), fromMap);
        for (FNConnection conn : connections) {
            int indexConn = conn.getFrom().getTrain().getIndexOfInterval(conn.getFrom());
            if (indexConn > index) {
                result.add(conn);
            }
        }
        return result;
    }

    public List<FNConnection> getTrainsFrom(TimeInterval fromInterval) {
        List<FNConnection> result = new LinkedList<FNConnection>();
        Collection<FNConnection> connections = this.get(fromInterval.getTrain(), fromMap);
        for (FNConnection conn : connections) {
            if (fromInterval == conn.getFrom()) {
                result.add(conn);
            }
        }
        return result;
    }

    private Collection<FNConnection> get(Train train, Map<Train, List<FNConnection>> map) {
        Collection<FNConnection> list = map.get(train);
        return list == null ? Collections.<FNConnection>emptyList() : list;
    }

    @Override
    public String toString() {
        return "FreightNet";
    }
}
