package net.parostroj.timetable.model;

import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.google.common.collect.FluentIterable;

import net.parostroj.timetable.model.freight.Connection;
import net.parostroj.timetable.model.freight.NodeConnection;
import net.parostroj.timetable.model.freight.NodeConnectionEdges;
import net.parostroj.timetable.model.freight.NodeConnectionNodes;
import net.parostroj.timetable.model.freight.DirectNodeConnection;
import net.parostroj.timetable.model.freight.FreightConnectionPath;
import net.parostroj.timetable.model.freight.TrainConnection;

class FreightRegionGraphDelegate {

    private TrainDiagram diagram;

    FreightRegionGraphDelegate(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    DirectedGraph<Node, DirectNodeConnection> getRegionGraph() {
        SimpleDirectedWeightedGraph<Node, DirectNodeConnection> graph = new SimpleDirectedWeightedGraph<>(DirectNodeConnection.class);
        diagram.getNet().getNodes().stream().filter(Node::isCenterOfRegions).forEach(graph::addVertex);
        for (Node node : graph.vertexSet()) {
            getRegionConnections(node).forEach(connection -> {
                Node n1 = connection.getFrom();
                Node n2 = connection.getTo().getNode();
                DirectNodeConnectionImpl edge = (DirectNodeConnectionImpl) graph.getEdge(n1, n2);
                if (edge == null) {
                    edge = new DirectNodeConnectionImpl(connection.getPath());
                    graph.addEdge(n1, n2, edge);
                } else {
                    edge.connections.add(connection.getPath());
                }
            });
        }
        return graph;
    }

    Stream<FreightConnectionPath> getRegionConnections(Node node) {
        Stream<FreightConnectionPath> list = stream(node.spliterator(), false)
                .filter(TimeInterval::isFreightFrom)
                .map(i -> diagram.getFreightNet().getFreightToNodes(i))
                .flatMap(l -> l.stream().filter(d -> d.getTo().isRegions()));
        return list;
    }

    Collection<NodeConnectionNodes> getRegionConnectionNodes() {
        BiFunction<NodeConnection, GraphPath<Node, DirectNodeConnection>, NodeConnectionNodes> conversion = (connect, path) -> new NodeConnectionNodesImpl(connect, FluentIterable.from(path.getEdgeList())
                .filter(conn -> conn.getTo() != path.getEndVertex())
                .transform(DirectNodeConnection::getTo)
                .toList());
        return this.computeRegionConnectionsImpl(this.getRegionGraph(), conversion);
    }

    Collection<NodeConnectionEdges> getRegionConnectionEdges() {
        BiFunction<NodeConnection, GraphPath<Node, DirectNodeConnection>, NodeConnectionEdges> conversion = (connect, path) -> new NodeConnectionEdgesImpl(connect, FluentIterable.from(path.getEdgeList())
                .filter(DirectNodeConnection.class)
                .toList());
        return this.computeRegionConnectionsImpl(this.getRegionGraph(), conversion);
    }

    protected <T extends NodeConnection> Collection<T> computeRegionConnectionsImpl(
            DirectedGraph<Node, DirectNodeConnection> graph,
            BiFunction<NodeConnection, GraphPath<Node, DirectNodeConnection>, T> conversion) {
        Collection<T> connections = new ArrayList<>();
        this.getAllConnectionVariants(graph).forEach(nodeConn -> {
                    DijkstraShortestPath<Node, DirectNodeConnection> path = new DijkstraShortestPath<>(graph,
                            nodeConn.getFrom(), nodeConn.getTo());
                    if (path.getPath() != null) {
                        connections.add(conversion.apply(nodeConn, path.getPath()));
                    }
                });
        return connections;
    }

    private Stream<NodeConnection> getAllConnectionVariants(DirectedGraph<Node, DirectNodeConnection> graph) {
        return graph.vertexSet().stream().flatMap(nodeFrom -> graph.vertexSet().stream().filter(node -> node != nodeFrom)
                .map(nodeTo -> new NodeConnectionImpl(nodeFrom, nodeTo)));
    }

    static class NodeConnectionImpl extends ConnectionImpl<Node, Node> implements NodeConnection {
        public NodeConnectionImpl(Node from, Node to) {
            super(from, to);
        }
    }

    static class TrainConnectionImpl extends ConnectionImpl<TimeInterval, TimeInterval> implements TrainConnection {
        public TrainConnectionImpl(TimeInterval from, TimeInterval to) {
            super(from, to);
        }
    }

    static class NodeConnectionNodesImpl implements NodeConnectionNodes {

        private NodeConnection connection;
        private List<Node> nodes;

        public NodeConnectionNodesImpl(NodeConnection connection, List<Node> nodes) {
            this.connection = connection;
            this.nodes = nodes;
        }

        @Override
        public Node getFrom() {
            return connection.getFrom();
        }

        @Override
        public Node getTo() {
            return connection.getTo();
        }

        @Override
        public List<Node> getIntermediateNodes() {
            return nodes;
        }

        @Override
        public String toString() {
            return connection.toString();
        }
    }

    static class NodeConnectionEdgesImpl implements NodeConnectionEdges {

        private NodeConnection connection;
        private List<DirectNodeConnection> edges;

        public NodeConnectionEdgesImpl(NodeConnection connection, List<DirectNodeConnection> edges) {
            this.connection = connection;
            this.edges = edges;
        }

        @Override
        public Node getFrom() {
            return connection.getFrom();
        }

        @Override
        public Node getTo() {
            return connection.getTo();
        }

        @Override
        public List<DirectNodeConnection> getEdges() {
            return edges;
        }

        @Override
        public String toString() {
            return connection.toString();
        }
    }

    static class ConnectionImpl<F, T> implements Connection<F, T> {

        private final F from;
        private final T to;

        public ConnectionImpl(F from, T to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public F getFrom() {
            return from;
        }

        @Override
        public T getTo() {
            return to;
        }

        @Override
        public String toString() {
            return String.format("<%s,%s>", getFrom(), getTo());
        }
    }

    static class DirectNodeConnectionImpl extends DefaultWeightedEdge implements DirectNodeConnection {

        protected final Set<List<TrainConnection>> connections;

        DirectNodeConnectionImpl(List<TrainConnection> connection) {
            this.connections = new HashSet<>();
            this.connections.add(connection);
        }

        @Override
        public double getWeight() {
            return this.computeWeight();
        }

        @Override
        public Node getTo() {
            return (Node) super.getTarget();
        }

        @Override
        public Node getFrom() {
            return (Node) super.getSource();
        }

        @Override
        public Set<List<TrainConnection>> getConnections() {
            return connections;
        }

        /*
         * Simplified weight -> plain running time without stop with one hour penalty (for manipulation).
         */
        private int computeWeight() {
            int weight = Integer.MAX_VALUE;
            for (List<TrainConnection> connection : connections) {
                // adding an hour -> simulates penalty for further transfer
                int time = connection.get(connection.size() - 1).getTo().getStart()
                        - connection.get(0).getFrom().getEnd() + TimeInterval.HOUR;
                if (time < weight) {
                    weight = time;
                }
            }
            return weight;
        }

        @Override
        public String toString() {
            return String.format("%s->%s:%d:%s", getSource(), getTarget(), computeWeight(),
                    connections.stream().map(cl -> getTrains(cl)).collect(joining(",")));
        }

        private String getTrains(List<TrainConnection> conn) {
            return conn.stream().map(c -> c.getFrom().getTrain().toString()).collect(joining(",", "[", "]"));
        }
    }
}
