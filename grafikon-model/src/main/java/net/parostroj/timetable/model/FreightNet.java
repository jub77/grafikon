package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.actions.FreightHelper;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.ListenableDirectedGraph;

/**
 * Managed freight data. Id is shared with train diagram.
 *
 * @author jub
 */
public class FreightNet implements Visitable, ObjectWithId, AttributesHolder {

    private final String id;
    private Attributes attributes;
    private final ListenableDirectedGraph<FNNode, FNConnection> netDelegate;
    private final AttributesListener attributesListener;
    private final GTListenerSupport<FreightNetListener, FreightNetEvent> listenerSupport;

    public FreightNet(String id) {
        this.id = id;
        this.netDelegate = new ListenableDirectedGraph<FNNode, FNConnection>(FNConnection.class);
        this.attributesListener = new AttributesListener() {
            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                FreightNetEvent event = null;
                if (attributes instanceof FNNode) {
                    event = new FreightNetEvent(FreightNet.this, GTEventType.FREIGHT_NET_NODE_ATTRIBUTE, change, (FNNode) attributes, null);
                } else if (attributes instanceof FNConnection) {
                    event = new FreightNetEvent(FreightNet.this, GTEventType.FREIGHT_NET_CONNECTION_ATTRIBUTE, change, null, (FNConnection) attributes);
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

    public FNNode addTrain(Train train) {
        FNNode node = new FNNode(train, attributesListener);
        this.addNodeImpl(node);
        return node;
    }

    public FNNode removeTrain(Train train) {
        FNNode found = this.getNodeImpl(train);
        this.removeNodeImpl(found);
        return found;
    }

    public FNConnection addConnection(TimeInterval from, TimeInterval to) {
        FNNode fromNode = this.getNodeImpl(from.getTrain());
        FNNode toNode = this.getNodeImpl(to.getTrain());
        FNConnection conn = new FNConnection(from, to, attributesListener);
        this.addConnectionImpl(fromNode, toNode, conn);
        return conn;
    }

    public FNConnection addConnection(FNNode fromNode, FNNode toNode, TimeInterval from, TimeInterval to) {
        FNConnection conn = new FNConnection(from, to, attributesListener);
        this.addConnectionImpl(fromNode, toNode, conn);
        return conn;
    }

    public FNConnection removeConnection(Train from, Train to) {
        FNNode fromNode = this.getNodeImpl(from);
        FNNode toNode = this.getNodeImpl(to);
        FNConnection conn = netDelegate.getEdge(fromNode, toNode);
        this.removeConnectionImpl(conn);
        return conn;
    }

    public void removeConnection(FNConnection conn) {
        this.removeConnectionImpl(conn);
    }

    public Collection<FNNode> getNodes() {
        return netDelegate.vertexSet();
    }

    public Collection<FNConnection> getConnections() {
        return netDelegate.edgeSet();
    }

    public FNNode getNode(Train train) {
        return this.getNodeImpl(train);
    }

    private FNNode getNodeImpl(Train train) {
        FNNode found = null;
        // get node with train
        for (FNNode node : netDelegate.vertexSet()) {
            if (node.getTrain() == train) {
                found = node;
                break;
            }
        }
        return found;
    }

    private void addConnectionImpl(FNNode from, FNNode to, FNConnection conn) {
        netDelegate.addEdge(from, to, conn);
        this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_CONNECTION_ADDED, null, conn));
    }

    private void removeConnectionImpl(FNConnection conn) {
        boolean removed = netDelegate.removeEdge(conn);
        if (removed) {
            this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_CONNECTION_REMOVED, null, conn));
        }
    }

    private void addNodeImpl(FNNode node) {
        netDelegate.addVertex(node);
        this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_TRAIN_ADDED, node, null));
    }

    private void removeNodeImpl(FNNode node) {
        if (node != null) {
            for (FNConnection conn : netDelegate.edgesOf(node)) {
                this.removeConnectionImpl(conn);
            }
            netDelegate.removeVertex(node);
            this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_TRAIN_REMOVED, node, null));
        }
    }

    public ListenableGraph<FNNode, FNConnection> getGraph() {
        return netDelegate;
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

    public List<Node> getFreightToNodes(TimeInterval fromInterval) {
        if (!fromInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals allowed.");
        }
        List<Node> result = new LinkedList<Node>();
        this.getFreightToNodesImpl(fromInterval, result);
        return result;
    }

    private void getFreightToNodesImpl(TimeInterval fromInterval, List<Node> result) {
        List<FNConnection> nextConns = getNextTrains(fromInterval);
        for (TimeInterval i : FreightHelper.getNodeIntervalsWithFreight(fromInterval.getTrain().getTimeIntervalList(), fromInterval)) {
            result.add(i.getOwnerAsNode());
            for (FNConnection conn : nextConns) {
                if (i == conn.getFrom()) {
                    this.getFreightToNodesImpl(conn.getTo(), result);
                }
            }
        }
    }

    public List<FNConnection> getNextTrains(TimeInterval fromInterval) {
        List<FNConnection> result = new LinkedList<FNConnection>();
        int index = fromInterval.getTrain().getIndexOfInterval(fromInterval);
        FNNode node = getNode(fromInterval.getTrain());
        Set<FNConnection> connections = netDelegate.outgoingEdgesOf(node);
        for (FNConnection conn : connections) {
            int indexConn = conn.getFrom().getTrain().getIndexOfInterval(conn.getFrom());
            if (indexConn > index) {
                result.add(conn);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "FreightNet";
    }
}
