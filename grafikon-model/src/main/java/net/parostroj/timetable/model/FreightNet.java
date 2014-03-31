package net.parostroj.timetable.model;

import java.util.Collection;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.ListenableDirectedGraph;

/**
 * Managed freight data.
 *
 * @author jub
 */
public class FreightNet implements Visitable, ObjectWithId {

    /**
     * Freight net node (train with possible additional attributes).
     *
     * @author jub
     */
    public class FreightNetNode extends Attributes {

        private final Train train;

        FreightNetNode(Train train, AttributesListener listener) {
            this.train = train;
            this.addListener(listener);
        }

        public Train getTrain() {
            return train;
        }
    }

    /**
     * Freight net node (train with possible additional attributes).
     *
     * @author jub
     */
    public class FreightNetConnection extends Attributes {

        private final Node node;
        private final Train fromTrain;
        private final Train toTrain;

        FreightNetConnection(Train fromTrain, Train toTrain, Node node, AttributesListener listener) {
            this.fromTrain = fromTrain;
            this.toTrain = toTrain;
            this.node = node;
            this.addListener(listener);
        }

        public Node getNode() {
            return node;
        }

        public Train getFromTrain() {
            return fromTrain;
        }

        public Train getToTrain() {
            return toTrain;
        }
    }

    private final TrainDiagram diagram;
    private final ListenableDirectedGraph<FreightNetNode, FreightNetConnection> netDelegate;
    private final AttributesListener attributesListener;
    private final GTListenerSupport<FreightNetListener, FreightNetEvent> listenerSupport;

    public FreightNet(TrainDiagram diagram) {
        this.diagram = diagram;
        this.netDelegate = new ListenableDirectedGraph<FreightNetNode, FreightNetConnection>(FreightNetConnection.class);
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
    }

    public void addTrain(Train train) {
        FreightNetNode node = new FreightNetNode(train, attributesListener);
        this.addNodeImpl(node);
    }

    public void removeTrain(Train train) {
        FreightNetNode found = this.getNodeImpl(train);
        this.removeNodeImpl(found);
    }

    public void addConnection(Train from, Train to) {
        this.addConnection(from, to, null);
    }

    public void addConnection(Train from, Train to, Node node) {
        FreightNetNode fromNode = this.getNodeImpl(from);
        FreightNetNode toNode = this.getNodeImpl(to);
        FreightNetConnection conn = new FreightNetConnection(from, to, node, attributesListener);
        this.addConnectionImpl(fromNode, toNode, conn);
    }

    public void removeConnection(Train from, Train to) {
        FreightNetNode fromNode = this.getNodeImpl(from);
        FreightNetNode toNode = this.getNodeImpl(to);
        this.removeConnectionImpl(netDelegate.getEdge(fromNode, toNode));
    }

    public Collection<FreightNetNode> getTrainNodes() {
        return netDelegate.vertexSet();
    }

    public Collection<FreightNetConnection> getConnections() {
        return netDelegate.edgeSet();
    }

    private FreightNetNode getNodeImpl(Train train) {
        FreightNetNode found = null;
        // get node with train
        for (FreightNetNode node : netDelegate.vertexSet()) {
            if (node.getTrain() == train) {
                found = node;
                break;
            }
        }
        return found;
    }

    private void addConnectionImpl(FreightNetNode from, FreightNetNode to, FreightNetConnection conn) {
        netDelegate.addEdge(from, to, conn);
        this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_CONNECTION_ADDED, conn));
    }

    private void removeConnectionImpl(FreightNetConnection conn) {
        boolean removed = netDelegate.removeEdge(conn);
        if (removed) {
            this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_CONNECTION_REMOVED, conn));
        }
    }

    private void addNodeImpl(FreightNetNode node) {
        netDelegate.addVertex(node);
        this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_TRAIN_ADDED, node));
    }

    private void removeNodeImpl(FreightNetNode node) {
        if (node != null) {
            for (FreightNetConnection conn : netDelegate.edgesOf(node)) {
                netDelegate.removeEdge(conn);
            }
            netDelegate.removeVertex(node);
            this.fireEvent(new FreightNetEvent(this, GTEventType.FREIGHT_NET_TRAIN_REMOVED, node));
        }
    }

    public DirectedGraph<FreightNetNode, FreightNetConnection> getGraph() {
        return netDelegate;
    }

    public TrainDiagram getDiagram() {
        return diagram;
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
        return null;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
