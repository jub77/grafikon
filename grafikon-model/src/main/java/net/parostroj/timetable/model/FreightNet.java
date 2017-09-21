package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import net.parostroj.timetable.model.events.AttributesListener;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.ListData;
import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.Observable;
import net.parostroj.timetable.model.freight.ConnectionStrategyType;
import net.parostroj.timetable.model.freight.FreightConnectionStrategy;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Managed freight data. Id is shared with train diagram.
 *
 * @author jub
 */
public class FreightNet
        implements Visitable, ObjectWithId, AttributesHolder, Observable, FreightNetAttributes, TrainDiagramPart {

    private static final ConnectionStrategyType DEFAULT_STRATEGY = ConnectionStrategyType.REGION;

    private final String id;
    private final TrainDiagram diagram;
    private final Attributes attributes;
    private final AttributesListener defaultAttributesListener;
    private final ListenerSupport listenerSupport;

    private final ListMultimap<TimeInterval, FNConnection> fromMap = ArrayListMultimap.create();
    private final ListMultimap<TimeInterval, FNConnection> toMap = ArrayListMultimap.create();
    private final Multimap<Train, FNConnection> fromTrainMap = HashMultimap.create();
    private final Multimap<Train, FNConnection> toTrainMap = HashMultimap.create();

    private FreightConnectionStrategy _strategy;

    FreightNet(String id, TrainDiagram diagram) {
        this.id = id;
        this.diagram = diagram;
        this.listenerSupport = new ListenerSupport();
        this.defaultAttributesListener = (attrs, change) -> {
            Event event = null;
            if (attrs instanceof FNConnection) {
                event = new Event(FreightNet.this, attrs, change);
            } else {
                // clean cached strategy object in case strategy changes
                if (change.checkName(ATTR_CONNECTION_STRATEGY_TYPE, ATTR_CUSTOM_CONNECTION_FILTER)) {
                    _strategy = null;
                }
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
            throw new IllegalArgumentException(
                    String.format("Connection moved to illegal place: %d (size %d)", indexTo, connList.size()));
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

    public FreightConnectionStrategy getConnectionStrategy() {
        return getStrategyImpl();
    }

    public List<FNConnection> getTrainsFrom(TimeInterval fromInterval) {
    	return fromMap.get(fromInterval);
    }

    public List<FNConnection> getTrainsTo(TimeInterval toInterval) {
    	return toMap.get(toInterval);
    }

    public ConnectionStrategyType getConnectionStrategyType() {
        ConnectionStrategyType strategyType = ConnectionStrategyType
                .fromString(getAttribute(ATTR_CONNECTION_STRATEGY_TYPE, String.class));
        return strategyType == null ? DEFAULT_STRATEGY : strategyType;
    }

    public void setConnectionStrategyType(ConnectionStrategyType strategyType) {
        setAttribute(ATTR_CONNECTION_STRATEGY_TYPE,
                strategyType != null && strategyType != DEFAULT_STRATEGY ? strategyType.getKey() : null);
    }

    @Override
    public String toString() {
        return String.format("FreightNet[connections=%d]", fromMap.size());
    }

    private FreightConnectionStrategy getStrategyImpl() {
        if (_strategy == null) {
            _strategy = FreightConnectionStrategy.create(getConnectionStrategyType(), diagram);
        }
        return _strategy;
    }
}
