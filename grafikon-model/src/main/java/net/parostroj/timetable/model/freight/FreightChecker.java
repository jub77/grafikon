package net.parostroj.timetable.model.freight;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.parostroj.timetable.model.Net;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.utils.Tuple;

/**
 * Checks if the freight is consistent - all node have freight connection and if there are connections
 * between centers.
 *
 * @author jub
 */
public class FreightChecker {

    private final FreightConnectionStrategy strategy;
    private final FreightAnalyser analyser;
    private final FreightConnectionAnalyser connAnalyser;

    public FreightChecker(final FreightConnectionStrategy strategy) {
        this.strategy = strategy;
        this.analyser = new FreightAnalyser(strategy);
        this.connAnalyser = new FreightConnectionAnalyser(this.analyser);
    }

    public Set<ConnectionState<NodeFreightConnection>> analyseNodeConnections(Net net, Collection<NodeType> skipTypes) {
        Collection<Tuple<Node>> allConns = this.getNodePermutation(net, skipTypes);

        return allConns.stream()
                .<ConnectionState<NodeFreightConnection>>map(t -> connAnalyser.analyse(t.first, t.second).stream()
                        .filter(NodeFreightConnection::isComplete)
                        .min(Comparator.comparingInt(NodeFreightConnection::getLength))
                        .map(fc -> new ConnectionImpl<>(t.first, t.second, fc))
                        .orElse(new ConnectionImpl<>(t.first, t.second)))
                .collect(toSet());
    }

    public Set<ConnectionState<NodeConnectionEdges>> analyseCenterConnections(Net net) {
        Collection<Tuple<Node>> allConns = this.getCenterPermutation(net);
        Map<Tuple<Node>, NodeConnectionEdges> map = strategy.getRegionConnectionEdges().stream()
                .collect(Collectors.toMap(c -> new Tuple<>(c.getFrom(), c.getTo()), Function.identity()));

        return allConns.stream()
                .<ConnectionState<NodeConnectionEdges>>map(t -> map.containsKey(t)
                        ? new ConnectionImpl<>(t.first, t.second, map.get(t)) : new ConnectionImpl<>(t.first, t.second))
                .collect(toSet());
    }

    public Set<Node> getNoConnectionsToNodes(Node center, Collection<NodeType> skipTypes) {
        if (!center.isCenterOfRegions()) {
            throw new IllegalArgumentException("Node " + center.getName() + " is not center");
        }

        // get list on nodes in the center
        Stream<Node> nodes = getNodesOfTheCenter(center, skipTypes);

        List<FreightConnectionPath> connections = getConnectionsFrom(center).collect(toList());

        nodes = nodes.filter(n -> !connections.stream().anyMatch(c -> c.getTo().isNode() && c.getTo().getNode() == n));

        return nodes.collect(toSet());
    }

    public Set<Node> getNoConnectionToCenter(Node center, Collection<NodeType> skipTypes) {
        if (!center.isCenterOfRegions()) {
            throw new IllegalArgumentException("Node " + center.getName() + " is not center");
        }

        Stream<Node> nodes = getNodesOfTheCenter(center, skipTypes);

        nodes = nodes.filter(n -> !getConnectionsFrom(n)
                .anyMatch(c -> c.getTo().isNode() && c.getTo().getNode() == center));

        return nodes.collect(toSet());
    }

    private Stream<Node> getNodesOfTheCenter(Node center, Collection<NodeType> skipTypes) {
        return center.getRegions().stream()
                .flatMap(r -> r.getNodes().stream())
                .distinct()
                .filter(n -> n != center && !skipTypes.contains(n.getType()));
    }

    private Stream<FreightConnectionPath> getConnectionsFrom(Node node) {
        return analyser.getFreightIntervalsFrom(node).stream()
                .flatMap(i -> strategy.getFreightToNodesNet(i).stream());
    }

    private Collection<Tuple<Node>> getCenterPermutation(Net net) {
        List<Node> centers = net.getNodes().stream()
                .filter(Node::isCenterOfRegions)
                .collect(toList());

        return centers.stream()
                .flatMap(n -> centers.stream()
                        .filter(c -> c != n)
                        .map(c -> new Tuple<>(n, c)))
                .collect(toSet());
    }

    private Collection<Tuple<Node>> getNodePermutation(Net net, Collection<NodeType> skipTypes) {
        // filter nodes
        List<Node> nodes = net.getNodes().stream()
                .filter(n -> !skipTypes.contains(n.getType()))
                .collect(toList());

        return nodes.stream()
                .flatMap(n -> nodes.stream()
                        .filter(c -> c != n)
                        .map(c -> new Tuple<>(n, c)))
                .collect(toSet());
    }

    public interface ConnectionState<T> extends NodeConnection {
        T getConnection();

        default boolean exists() {
            return getConnection() != null;
        }
    }

    private static class ConnectionImpl<T> implements ConnectionState<T> {

        private final Node from;
        private final Node to;
        private final T conns;

        public ConnectionImpl(Node from, Node to) {
            this(from, to, null);
        }

        public ConnectionImpl(Node from, Node to, T conns) {
            this.from = from;
            this.to = to;
            this.conns = conns;
        }

        @Override
        public Node getFrom() {
            return from;
        }

        @Override
        public Node getTo() {
            return to;
        }

        @Override
        public T getConnection() {
            return conns;
        }

        @Override
        public String toString() {
            return String.format("%s-%s[%s]", from, to, exists());
        }
    }
}
