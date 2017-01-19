package net.parostroj.timetable.model.freight;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Pair;

/**
 * @author jub
 */
public class FreightAnalyser {

    private final TrainDiagram diagram;

    public FreightAnalyser(final TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public List<TimeInterval> getFreightIntervalsFrom(Node node) {
        return StreamSupport.stream(node.spliterator(), true).filter(i -> !i.isTechnological() && i.isFreightFrom())
                .sorted(this::compareNormalizedStarts).collect(Collectors.toList());
    }

    public NodeFreight getFreightNodesFrom(Node node) {
        Map<FreightConnection, Set<TimeInterval>> map = getFreightIntervalsFrom(node).stream()
                .flatMap(i -> diagram.getFreightNet().getFreightToNodes(i).stream()
                        .map(d -> new Pair<>(FreightFactory.createFreightNodeConnection(d.getFrom(), d.getTo()), i)))
                .collect(Collectors.groupingBy(p -> p.first, Collectors.mapping(p -> p.second, Collectors.toSet())));
        Set<FreightConnectionVia> set = map.entrySet().stream()
                .map(e -> FreightFactory.createFreightNodeConnection(e.getKey().getFrom(), e.getKey().getTo(),
                        new TransportImpl(null, e.getValue())))
                .collect(Collectors.toSet());
        return new NodeFreightImpl(set, Collections.emptySet());
    }

    public NodeFreight getFreightNodeRegionsFrom(Node node) {
        return getFreightNodeRegionsFrom(node, getFreightNodesFrom(node));
    }

    public NodeFreight getFreightNodeRegionsFrom(Node node, NodeFreight nodeTransport) {
        // depending if the node is center of regions or not
        Collection<NodeConnectionNodes> connections = diagram.getFreightNet().getRegionConnectionNodes();
        Map<Node, FreightConnectionVia> nodes = nodeTransport.getDirectConnectionsMap();
        if (node.isCenterOfRegions()) {
            Stream<NodeConnectionNodes> targets = connections.stream().filter(c -> c.getFrom() == node);
            Stream<FreightConnectionVia> conns = targets.map(t -> {
                Node intermediateNode = t.getFirstIntermediateNode();
                Node endNode = t.getTo();
                Transport transport = intermediateNode == null ? nodes.get(endNode).getTransport() : new TransportImpl(nodes.get(intermediateNode), null);
                return FreightFactory.createFreightNodeConnection(node, FreightFactory.createFreightDestination(endNode), transport);
            });
            Set<FreightConnectionVia> regionSet = conns.collect(Collectors.toSet());
            return new NodeFreightImpl(nodeTransport.getDirectConnections(), regionSet);
        } else {
            // get direct connection map to centers
            Map<Node, FreightConnectionVia> centers = nodeTransport.getDirectConnections().stream()
                    .filter(c -> c.getTo().isCenter())
                    .collect(Collectors.toMap(c -> c.getTo().getNode(), c -> c));
            // filter region connections to connections started from reachable direct connections and
            // do not lead to reachable region
            Stream<NodeConnectionNodes> targets = connections.stream()
                    .filter(c -> centers.containsKey(c.getFrom()))
                    .filter(c -> !centers.containsKey(c.getTo()));
            Stream<FreightConnectionVia> conns = targets.map(t -> {
                Transport transport = new TransportImpl(nodes.get(t.getFrom()), null);
                return FreightFactory.createFreightNodeConnection(node, FreightFactory.createFreightDestination(t.getTo()), transport);
            });
            conns = Stream.concat(conns, centers.values().stream());
            Set<FreightConnectionVia> regionSet = conns.collect(Collectors.toSet());
            return new NodeFreightImpl(nodeTransport.getDirectConnections(), regionSet);
        }
    }

    protected int compareNormalizedStarts(TimeInterval i1, TimeInterval i2) {
        return Integer.compare(i1.getInterval().getNormalizedStart(), i2.getInterval().getNormalizedStart());
    }

    // returns super region - the assumption is that regions share the same super region
    public static Region getSuperRegion(Collection<? extends Region> regions) {
        return regions.isEmpty() ? null : regions.iterator().next().getSuperRegion();
    }

    /**
     * @param fromRegions starting regions
     * @param toRegions destination regions
     * @return destination regions based on regions bellow the first common region
     */
    public static Set<Region> transformToRegions(Set<Region> fromRegions, Set<Region> toRegions) {
        Region toSuper = getSuperRegion(toRegions);
        Region fromSuper = getSuperRegion(fromRegions);
        // all center regions has to have the same super region (if exists)
        if (!toRegions.isEmpty() && !fromRegions.isEmpty()) {
            if (fromSuper == null && toSuper != null) {
                toRegions = Collections.singleton(toSuper.getTopSuperRegion());
            } else if (toSuper != null && !fromSuper.containsInHierarchy(toSuper)) {
                Region dest = toSuper;
                while (dest.getSuperRegion() != null && !fromSuper.containsInHierarchy(dest.getSuperRegion())) {
                    dest = dest.getSuperRegion();
                }
                toRegions = Collections.singleton(dest);
            }
        }
        return toRegions;
    }

    public static Region getFirstCommonRegion(Collection<? extends Region> fromRegions, Collection<? extends Region> toRegions) {
        Region toSuper = getSuperRegion(toRegions);
        Region fromSuper = getSuperRegion(fromRegions);

        Region commonRegion = null;

        if (fromSuper != null && toSuper != null) {
            commonRegion = fromSuper;
            while (commonRegion != null && !toSuper.containsInHierarchy(commonRegion)) {
                commonRegion = commonRegion.getSuperRegion();
            }
        }

        return commonRegion;
    }

    private static class NodeFreightImpl implements NodeFreight {

        private final Set<FreightConnectionVia> directConnections;
        private final Set<FreightConnectionVia> regionConnections;
        private Map<Node, FreightConnectionVia> directMap;

        private NodeFreightImpl(Set<FreightConnectionVia> directConnections, Set<FreightConnectionVia> regionConnections) {
            this.directConnections = directConnections;
            this.regionConnections = regionConnections;
        }

        @Override
        public Set<FreightConnectionVia> getDirectConnections() {
            return directConnections;
        }

        @Override
        public Set<FreightConnectionVia> getRegionConnections() {
            return regionConnections;
        }

        @Override
        public String toString() {
            return directConnections.toString() + regionConnections.toString();
        }

        @Override
        public Map<Node, FreightConnectionVia> getDirectConnectionsMap() {
            if (directMap == null) {
                directMap = directConnections.stream()
                        .filter(c -> c.getTo().isNode())
                        .collect(Collectors.toMap(t -> t.getTo().getNode(), t -> t));
            }
            return directMap;
        }
    }

    public static class TransportImpl implements Transport {

        private final FreightConnection connection;
        private final Set<TimeInterval> trains;

        private TransportImpl(FreightConnection connection, Set<TimeInterval> trains) {
            this.connection = connection;
            this.trains = trains;
        }

        @Override
        public FreightConnection getConnection() {
            return connection;
        }

        @Override
        public Set<TimeInterval> getTrains() {
            return trains;
        }

        @Override
        public String toString() {
            return isDirect() ? trains.toString() : connection.toString();
        }
    }
}
