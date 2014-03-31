package net.parostroj.timetable.model;

import java.util.Collection;

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
public class FreightNet implements Visitable, ObjectWithId {

    /**
     * Freight net node (train with possible additional attributes).
     *
     * @author jub
     */
    public class FreightNetNode extends Attributes implements ObjectWithId, Visitable {

        private final Train train;
        private Location location;

        FreightNetNode(Train train, AttributesListener listener) {
            this.train = train;
            this.addListener(listener);
        }

        public Train getTrain() {
            return train;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }

        @Override
        public String getId() {
            return train.getId();
        }

        @Override
        public void accept(TrainDiagramVisitor visitor) {
            visitor.visit(train);
        }

        @Override
        public String toString() {
            return train.getName();
        }
    }

    /**
     * Freight net node (train with possible additional attributes).
     *
     * @author jub
     */
    public class FreightNetConnection extends Attributes implements ObjectWithId, Visitable {

        private final TimeInterval from;
        private final TimeInterval to;

        FreightNetConnection(TimeInterval from, TimeInterval to, AttributesListener listener) {
            this.from = from;
            this.to = to;
            this.addListener(listener);
        }

        @Override
        public String getId() {
            return from.getTrain().getId();
        }

        public TimeInterval getFrom() {
            return from;
        }

        public TimeInterval getTo() {
            return to;
        }

        @Override
        public void accept(TrainDiagramVisitor visitor) {
            visitor.visit(from.getOwnerAsNode());
        }

        @Override
        public String toString() {
            return from.getOwnerAsNode().getAbbr();
        }
    }

    private final String id;
    private final TrainDiagram diagram;
    private final ListenableDirectedGraph<FreightNetNode, FreightNetConnection> netDelegate;
    private final AttributesListener attributesListener;
    private final GTListenerSupport<FreightNetListener, FreightNetEvent> listenerSupport;

    public FreightNet(TrainDiagram diagram) {
        this.id = diagram.getId();
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

    public void addConnection(TimeInterval from, TimeInterval to) {
        FreightNetNode fromNode = this.getNodeImpl(from.getTrain());
        FreightNetNode toNode = this.getNodeImpl(to.getTrain());
        FreightNetConnection conn = new FreightNetConnection(from, to, attributesListener);
        this.addConnectionImpl(fromNode, toNode, conn);
    }

    public void addConnection(FreightNetNode fromNode, FreightNetNode toNode, TimeInterval from, TimeInterval to) {
        this.addConnectionImpl(fromNode, toNode, new FreightNetConnection(from, to, attributesListener));
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

    public FreightNetNode getNode(Train train) {
        return this.getNodeImpl(train);
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

    public ListenableGraph<FreightNetNode, FreightNetConnection> getGraph() {
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
        return id;
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }
}
