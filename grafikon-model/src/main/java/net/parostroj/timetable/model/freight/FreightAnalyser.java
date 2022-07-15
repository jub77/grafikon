package net.parostroj.timetable.model.freight;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static net.parostroj.timetable.utils.ObjectsUtil.intersects;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.RegionHierarchy;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.TimeUtil;

/**
 * @author jub
 */
public class FreightAnalyser {

    private final FreightConnectionStrategy strategy;

    public FreightAnalyser(final FreightConnectionStrategy strategy) {
        this.strategy = strategy;
    }

    public FreightConnectionStrategy getConnectionStrategy() {
        return strategy;
    }

    public List<TimeInterval> getFreightIntervalsFrom(Node node) {
        return StreamSupport.stream(node.spliterator(), true).filter(i -> !i.isTechnological() && i.isFreightFrom())
                .sorted(TimeUtil::compareNormalizedStarts).collect(toList());
    }

    public List<TimeInterval> getFreightTrainUnitIntervals(final Node node) {
        final TrainsCycleType type = node.getDiagram().getTrainUnitCycleType();
        return StreamSupport.stream(node.spliterator(), true)
                .filter(TimeInterval::isFirst)
                .filter(i -> i.getTrain().getCycleItemsForInterval(type, i).stream()
                        .anyMatch(item -> item.getCycle().getAttributeAsBool(TrainsCycle.ATTR_FREIGHT)))
                .sorted(TimeUtil::compareNormalizedStarts)
                .collect(toList());
    }

    public NodeFreight getNodeFreightFrom(Node node) {
        // getting straight connections
        Map<FreightConnection, Set<TimeInterval>> strainghtConnectionMap = getFreightIntervalsFrom(node).stream()
                .flatMap(i -> strategy.getFreightToNodesNet(i).stream()
                        .map(d -> new Pair<>(FreightFactory.createFreightNodeConnection(d.getFrom(), d.getTo()), i)))
                .collect(groupingBy(p -> p.first, mapping(p -> p.second, toSet())));
        Set<FreightConnectionVia> straightConnections = strainghtConnectionMap.entrySet().stream()
                .map(e -> FreightFactory.createFreightNodeConnection(e.getKey().getFrom(), e.getKey().getTo(),
                        new TransportImpl(null, e.getValue())))
                .collect(toSet());

        // depending if the node is center of regions or not
        Collection<NodeConnectionNodes> centerConnections = strategy.getRegionConnectionNodes();
        Stream<FreightConnectionVia> conns;
        if (node.isCenterOfRegions()) {
            Map<Node, FreightConnectionVia> nodes = this.getToCenterMap(straightConnections);
            // filter out region connections which are not from current node and ends in nodes with straight connection
            Stream<NodeConnectionNodes> targets = centerConnections.stream()
                    .filter(c -> c.getFrom() == node)
                    .filter(c -> !nodes.containsKey(c.getTo()));
            conns = targets.map(t -> {
                Node intermediateNode = t.getFirstIntermediateNode();
                Node endNode = t.getTo();
                Transport transport = new TransportImpl(nodes.get(intermediateNode).getTo().getRegions(), null);
                FreightDestination destination = FreightFactory.createFreightDestination(node, endNode.getCenterRegionHierarchy());
                return FreightFactory.createFreightNodeConnection(node, destination, transport);
            });
        } else {
            Map<Node, FreightConnectionVia> nodes = this.getToCenterMap(straightConnections, node);
            // filter region connections to connections started from reachable straight connections and
            // do not lead to reachable region
            Stream<NodeConnectionNodes> targets = centerConnections.stream()
                    .filter(c -> nodes.containsKey(c.getFrom()))
                    .filter(c -> !nodes.containsKey(c.getTo()));
            conns = targets.map(t -> {
                Node endNode = t.getTo();
                Transport transport = new TransportImpl(nodes.get(t.getFrom()).getTo().getRegions(), null);
                FreightDestination destination = FreightFactory.createFreightDestination(node, endNode.getCenterRegionHierarchy());
                return FreightFactory.createFreightNodeConnection(node, destination, transport);
            });
        }
        // filter connections where to regions are the same as via (transport) regions
        conns = conns.filter(fc -> !fc.getTransport().getRegions().equals(fc.getTo().getRegions()));
        // concat straight connections and connections via
        Stream<FreightConnectionVia> allConns = Stream.concat(conns, straightConnections.stream());

        Map<FreightDestination, Transport> allConnsMap = allConns
                .collect(toMap(FreightConnectionVia::getTo, FreightConnectionVia::getTransport, Transport::merge));

        Set<FreightConnectionVia> allConnections = allConnsMap.entrySet().stream()
                .map(e -> FreightFactory.createFreightNodeConnection(node, e.getKey(), e.getValue()))
                .collect(toSet());
        return new NodeFreightImpl(node, allConnections);
    }

    private Map<Node, FreightConnectionVia> getToCenterMap(Set<FreightConnectionVia> connections) {
        return connections.stream()
                .filter(c -> c.getTo().isRegionsDestination())
                .collect(toMap(c -> c.getTo().getNode(), Function.identity()));
    }

    // filter out only centers within the same region as node
    private Map<Node, FreightConnectionVia> getToCenterMap(Set<FreightConnectionVia> connections, Node node) {
        return connections.stream()
                .filter(c -> intersects(node.getRegions(), c.getTo().getRegions()))
                .collect(toMap(c -> c.getTo().getNode(), Function.identity()));
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
                colors = Stream.concat(regionColors, colors.stream()).collect(toSet());
            } else {
                // different freight color regions - get colors from color map
                if (fromFC != null && toFC != null) {
                    Stream<FreightColor> regionMapColors = fromFC.getFreightColorMap().entrySet().stream()
                            .filter(e -> e.getValue() == toFC)
                            .map(Map.Entry::getKey);
                    colors = Stream.concat(regionMapColors, colors.stream()).collect(toSet());
                }
            }
        }
        return colors;
    }
}
