package net.parostroj.timetable.model;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.google.common.collect.FluentIterable;

class FreightRegionGraphDelegate {

    private TrainDiagram diagram;

    FreightRegionGraphDelegate(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    DirectedGraph<Node, RegionFreightConnection> getRegionGraph() {
        SimpleDirectedWeightedGraph<Node, RegionFreightConnection> graph = new SimpleDirectedWeightedGraph<>(RegionFreightConnection.class);
        diagram.getNet().getNodes().stream().filter(node -> node.isCenterOfRegions())
                .forEach(node -> graph.addVertex(node));
        for (Node node : graph.vertexSet()) {
            StreamSupport.stream(node.spliterator(), false)
                    .filter(interval -> interval.isFreightFrom() && !interval.isLast())
                    .flatMap(interval -> interval.getTrain().getIntervals(interval.getTrainInterval(2), null).stream()
                            .filter(i -> i.isRegionCenterTransfer()).map(i -> new TrainFreightConnection(interval, i)))
                    .forEach(connection -> {
                        Node n1 = connection.getFrom().getOwnerAsNode();
                        Node n2 = connection.getTo().getOwnerAsNode();
                        RegionFreightConnection edge = graph.getEdge(n1, n2);
                        if (edge == null) {
                            edge = new RegionFreightConnection(connection);
                            graph.addEdge(n1, n2, edge);
                        } else {
                            edge.connections.add(connection);
                        }
                    });
        }
        return graph;
    }

    Map<NodeConnection, List<Node>> getRegionConnectionNodes() {
        return this.computeRegionConnectionsImpl(this.getRegionGraph(),
                        path -> FluentIterable.from(path.getEdgeList())
                                .filter(conn -> conn.getTo() != path.getEndVertex())
                                .transform(conn -> conn.getTo()).toList());
    }

    Map<NodeConnection, List<RegionConnection>> getRegionConnectionEdges() {
        return this.computeRegionConnectionsImpl(this.getRegionGraph(),
                path -> FluentIterable.from(path.getEdgeList()).filter(RegionConnection.class).toList());
    }

    protected <T> Map<NodeConnection, T> computeRegionConnectionsImpl(
            DirectedGraph<Node, RegionFreightConnection> graph,
            Function<GraphPath<Node, RegionFreightConnection>, T> conversion) {
        Map<NodeConnection, T> connections = new HashMap<>();
        this.getAllConnectionVariants(graph).forEach(nodeConn -> {
                    DijkstraShortestPath<Node, RegionFreightConnection> path = new DijkstraShortestPath<>(graph,
                            nodeConn.getFrom(), nodeConn.getTo());
                    if (path.getPath() != null) {
                        connections.put(nodeConn, conversion.apply(path.getPath()));
                    }
                });
        return connections;
    }

    private Stream<NodeConnection> getAllConnectionVariants(DirectedGraph<Node, RegionFreightConnection> graph) {
        return graph.vertexSet().stream().flatMap(nodeFrom -> graph.vertexSet().stream().filter(node -> node != nodeFrom)
                .map(nodeTo -> new NodeFreightConnection(nodeFrom, nodeTo)));
    }

    static class NodeFreightConnection extends FreightConnection<Node> implements NodeConnection {
        public NodeFreightConnection(Node from, Node to) {
            super(from, to);
        }
    }

    static class TrainFreightConnection extends FreightConnection<TimeInterval> implements TrainConnection {
        public TrainFreightConnection(TimeInterval from, TimeInterval to) {
            super(from, to);
        }
    }

    static class FreightConnection<T> implements Connection<T> {

        private final T from;
        private final T to;

        public FreightConnection(T from, T to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public T getFrom() {
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

    static class RegionFreightConnection extends DefaultWeightedEdge implements RegionConnection {

        protected final Collection<TrainConnection> connections;

        RegionFreightConnection(TrainConnection connection) {
            this.connections = new ArrayList<>();
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
        public Collection<TrainConnection> getConnections() {
            return connections;
        }

        /*
         * Simplified weight -> plain running time without stop with one hour penalty (for manipulation).
         */
        private int computeWeight() {
            int weight = Integer.MAX_VALUE;
            for (TrainConnection connection : connections) {
                // adding an hour -> simulates penalty for further transfer
                int connSpeed = connection.getFrom().getTrain().getIntervals(connection.getFrom(), connection.getTo())
                    .stream().filter(i -> i.isNodeOwner()).collect(Collectors.summingInt(i -> i.getLength())) + TimeInterval.HOUR;
                if (connSpeed < weight) {
                    weight = connSpeed;
                }
            }
            return weight;
        }

        @Override
        public String toString() {
            return String.format("%s->%s:%d:%s", getSource(), getTarget(), computeWeight(),
                    connections.stream().map(i -> i.getFrom().getTrain().toString()).collect(Collectors.joining(",")));
        }
    }
}
