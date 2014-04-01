package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
                listenerSupport.fireEvent(new FreightNetEvent(FreightNet.this, change, attributes));
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
        this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_CONNECTION_ADDED, conn));
    }

    private void removeConnectionImpl(FNConnection conn) {
        boolean removed = netDelegate.removeEdge(conn);
        if (removed) {
            this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_CONNECTION_REMOVED, conn));
        }
    }

    private void addNodeImpl(FNNode node) {
        netDelegate.addVertex(node);
        this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_TRAIN_ADDED, node));
    }

    private void removeNodeImpl(FNNode node) {
        if (node != null) {
            for (FNConnection conn : netDelegate.edgesOf(node)) {
                this.removeConnectionImpl(conn);
            }
            netDelegate.removeVertex(node);
            this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_TRAIN_REMOVED, node));
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

    public List<net.parostroj.timetable.model.Node> getFreightToNodes(Train train, TimeInterval interval) {
        // TODO implementaiton
        return Collections.emptyList();
    }
}
