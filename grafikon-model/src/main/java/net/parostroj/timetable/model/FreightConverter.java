package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import net.parostroj.timetable.actions.TextList;
import net.parostroj.timetable.utils.Tuple;

/**
 * Conversion to string representation.
 *
 * @author jub
 */
public class FreightConverter {

    public String freightDstListToString(Collection<FreightDst> list) {
        StringBuilder builder = new StringBuilder();
        TextList output = new TextList(builder, ",");
        output.addItems(list);
        output.finish();
        return builder.toString();
    }

    public void computeRegionGraph(TrainDiagram diagram) {
        SimpleDirectedWeightedGraph<Node, GEdge> graph = new SimpleDirectedWeightedGraph<>(GEdge.class);
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
                        GEdge edge = graph.getEdge(n1, n2);
                        if (edge == null) {
                            edge = new GEdge(connection);
                            graph.addEdge(n1, n2, edge);
                        } else {
                            edge.connections.add(connection);
                        }
                    });
        }

        Stream<Tuple<Node>> map = graph.vertexSet().stream().flatMap(v -> graph.vertexSet().stream().filter(v2 -> v2 != v).map(v3 -> new Tuple<>(v, v3)));
        List<Tuple<Node>> ll = map.collect(Collectors.toList());
        System.out.println(ll);
        for (Tuple<Node> t : ll) {
            DijkstraShortestPath<Node, GEdge> d = new DijkstraShortestPath<Node, FreightConverter.GEdge>(graph, t.first, t.second);
            System.out.println("P: " + t + " > "+ d.getPath());
        }
    }

    public static class GEdge extends DefaultWeightedEdge {

        protected final Collection<Tuple<TimeInterval>> connections;

        public GEdge() {
            this.connections = Collections.emptyList();
        }

        public GEdge(Tuple<TimeInterval> connection) {
            this.connections = new ArrayList<>();
            this.connections.add(connection);
        }

        @Override
        public double getWeight() {
            return this.computeWeight();
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
