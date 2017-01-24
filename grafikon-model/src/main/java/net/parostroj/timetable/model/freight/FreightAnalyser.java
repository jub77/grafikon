package net.parostroj.timetable.model.freight;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.RegionHierarchy;
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
        Stream<FreightConnectionVia> conns;
        if (node.isCenterOfRegions()) {
            Stream<NodeConnectionNodes> targets = connections.stream().filter(c -> c.getFrom() == node);
            conns = targets.map(t -> {
                Node intermediateNode = t.getFirstIntermediateNode();
                Node endNode = t.getTo();
                Transport transport = intermediateNode == null ? nodes.get(endNode).getTransport()
                        : new TransportImpl(nodes.get(intermediateNode), null);
                return FreightFactory.createFreightNodeConnection(node,
                        FreightFactory.createFreightDestination(node, endNode.getCenterRegionHierarchy()), transport);
            });
        } else {
            // get direct connection map to centers
            Map<Node, FreightConnectionVia> centers = nodeTransport.getDirectConnections().stream()
                    .filter(c -> c.getTo().isRegions())
                    .collect(Collectors.toMap(c -> c.getTo().getNode(), c -> c));
            // filter region connections to connections started from reachable direct connections and
            // do not lead to reachable region
            Stream<NodeConnectionNodes> targets = connections.stream()
                    .filter(c -> centers.containsKey(c.getFrom()))
                    .filter(c -> !centers.containsKey(c.getTo()));
            conns = targets.map(t -> {
                Transport transport = new TransportImpl(nodes.get(t.getFrom()), null);
                FreightDestination destination = FreightFactory.createFreightDestination(node,
                        t.getTo().getCenterRegionHierarchy());
                FreightConnectionVia createFreightNodeConnection = FreightFactory.createFreightNodeConnection(node,
                        destination, transport);
                return createFreightNodeConnection;
            });
            conns = Stream.concat(conns, centers.values().stream());
        }
        // filter connections where to regions are the same as via (transport) regions
        conns = conns.filter(fc -> fc.getTransport().isDirect()
                || !fc.getTransport().getConnection().getTo().getRegions().equals(fc.getTo().getRegions()));
        // filter duplicates
        conns = conns.distinct();
        Set<FreightConnectionVia> regionSet = conns.collect(Collectors.toSet());
        return new NodeFreightImpl(nodeTransport.getDirectConnections(), regionSet);
    }

    protected int compareNormalizedStarts(TimeInterval i1, TimeInterval i2) {
        return Integer.compare(i1.getInterval().getNormalizedStart(), i2.getInterval().getNormalizedStart());
    }

    /**
     * @param fromRegions starting regions
     * @param toRegions destination regions
     * @return destination regions based on regions bellow the first common region
     */
    public static Set<Region> transformToRegions(RegionHierarchy fromRegions, RegionHierarchy toRegions) {
        if (toRegions == null || toRegions.getRegions().isEmpty()) return Collections.emptySet();
        Region toSuper = toRegions.getFirstSuperRegion();
        Region fromSuper = fromRegions.getFirstSuperRegion();
        Set<Region> result = toRegions.getRegions();
        // all center regions has to have the same super region (if exists)
        if (!toRegions.getRegions().isEmpty() && !fromRegions.getRegions().isEmpty()) {
            if (fromSuper == null && toSuper != null) {
                result = Collections.singleton(toRegions.getTopSuperRegion());
            } else if (toSuper != null && !fromRegions.findInSuperRegions(r -> r == toSuper).isPresent()) {
                Region dest = toSuper;
                while (dest.getSuperRegion() != null
                        && !fromRegions.findInSuperRegions(Predicate.isEqual(dest.getSuperRegion())).isPresent()) {
                    dest = dest.getSuperRegion();
                }
                result = Collections.singleton(dest);
            }
        }
        return result;
    }

    /**
     * Extracts information about colors.
     *
     * @param fromNode from node
     * @param toNode to node (if exists, can be null)
     * @param toRegions target regions (can be empty if the target is node only
     * @return set of colors for the connection
     */
    public static Set<FreightColor> getFreightColors(Node fromNode, Node toNode, Set<Region> toRegions) {
        Set<FreightColor> colors = Collections.emptySet();
        Region fromFC = fromNode.getRegionHierarchy().getFreightColorRegion();
        if (toNode != null && !toNode.getFreightColors().isEmpty()) {
            // node colors ...
            Region toFC = toNode.getRegionHierarchy().getFreightColorRegion();
            if (fromFC == toFC) {
                // in the same region - use node freight color ...
                colors = toNode.getFreightColors();
            }
        }
        if (!toRegions.isEmpty()) {
            Region toFC = RegionHierarchy.from(toRegions).getFreightColorRegion();
            if (fromFC == toFC) {
                // the same freight color region - get all freight colors from regions
                Stream<FreightColor> regionColors = toRegions.stream()
                        .flatMap(r -> r.getAllNodes().stream())
                        .flatMap(n -> n.getFreightColors().stream());
                colors = Stream.concat(regionColors, colors.stream()).collect(Collectors.toSet());
            } else {
                // different freight color regions - get colors from color map
                if (fromFC != null && toFC != null) {
                    Stream<FreightColor> regionMapColors = fromFC.getFreightColorMap().entrySet().stream()
                            .filter(e -> e.getValue() == toFC)
                            .map(e -> e.getKey());
                    colors = Stream.concat(regionMapColors, colors.stream()).collect(Collectors.toSet());
                }
            }
        }
        return colors;
    }

    private static class NodeFreightImpl implements NodeFreight {

        private final Set<FreightConnectionVia> directConnections;
        private final Set<FreightConnectionVia> regionConnections;
        private Map<Node, FreightConnectionVia> directMap;
        private Set<FreightConnectionVia> freightColorConnections;

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

        @Override
        public Set<FreightConnectionVia> getFreightColorConnections() {
            if (freightColorConnections == null) {
                // filter direct connections which are not region (they appear also in region list)
                Stream<FreightConnectionVia> directStream = directConnections.stream()
                        .filter(c -> !c.getTo().isRegions());
                // combine region and director connections and filter them for freight colors
                Stream<FreightConnectionVia> stream = Stream.concat(directStream, regionConnections.stream())
                        .filter(c -> c.getTo().isFreightColors());
                freightColorConnections = stream.distinct().collect(Collectors.toSet());
            }
            return freightColorConnections;
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
