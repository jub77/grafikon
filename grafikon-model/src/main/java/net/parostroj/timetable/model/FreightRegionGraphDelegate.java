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

import net.parostroj.timetable.utils.Tuple;

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
                            .filter(i -> i.isRegionCenterTransfer()).map(i -> new Tuple<>(interval, i)))
                    .forEach(connection -> {
                        Node n1 = connection.first.getOwnerAsNode();
                        Node n2 = connection.second.getOwnerAsNode();
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

    Map<Tuple<Node>, List<Node>> getRegionConnectionNodes() {
        return this.computeRegionConnectionsImpl(this.getRegionGraph(),
                        path -> FluentIterable.from(path.getEdgeList())
                                .filter(conn -> conn.getTo() != path.getEndVertex())
                                .transform(conn -> conn.getTo()).toList());
    }

    Map<Tuple<Node>, List<RegionConnection>> getRegionConnectionEdges() {
        return this.computeRegionConnectionsImpl(this.getRegionGraph(),
                path -> FluentIterable.from(path.getEdgeList()).filter(RegionConnection.class).toList());
    }

    protected <T> Map<Tuple<Node>, T> computeRegionConnectionsImpl(
            DirectedGraph<Node, RegionFreightConnection> graph,
            Function<GraphPath<Node, RegionFreightConnection>, T> conversion) {
        Map<Tuple<Node>, T> connections = new HashMap<>();
        this.getAllConnectionVariants(graph).forEach(tuple -> {
                    DijkstraShortestPath<Node, RegionFreightConnection> path = new DijkstraShortestPath<>(graph,
                            tuple.first, tuple.second);
                    if (path.getPath() != null) {
                        connections.put(tuple, conversion.apply(path.getPath()));
                    }
                });
        return connections;
    }

    private Stream<Tuple<Node>> getAllConnectionVariants(DirectedGraph<Node, RegionFreightConnection> graph) {
        return graph.vertexSet().stream().flatMap(nodeFrom -> graph.vertexSet().stream().filter(node -> node != nodeFrom)
                .map(nodeTo -> new Tuple<>(nodeFrom, nodeTo)));
    }

    static class RegionFreightConnection extends DefaultWeightedEdge implements RegionConnection {

        protected final Collection<Tuple<TimeInterval>> connections;

        RegionFreightConnection(Tuple<TimeInterval> connection) {
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
        public Collection<Tuple<TimeInterval>> getConnections() {
            return connections;
        }

        /*
         * Simplified weight -> plain running time without stop with one hour penalty (for manipulation).
         */
        private int computeWeight() {
            int weight = Integer.MAX_VALUE;
            for (Tuple<TimeInterval> connection : connections) {
                // adding an hour -> simulates penalty for further transfer
                int connSpeed = connection.first.getTrain().getIntervals(connection.first, connection.second)
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
                    connections.stream().map(i -> i.first.getTrain().toString()).collect(Collectors.joining(",")));
        }
    }
}
