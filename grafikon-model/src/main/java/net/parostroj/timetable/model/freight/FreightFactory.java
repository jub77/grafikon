package net.parostroj.timetable.model.freight;

import java.util.List;
import java.util.Set;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.RegionHierarchy;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Factory for creating destinations.
 *
 * @author jub
 */
public class FreightFactory {

    public static FreightConnectionPath createFreightNodeConnection(Node fromNode, Node toNode,
            TimeInterval timeInterval, List<TimeInterval> path) {
        return new FreightConnectionPathImpl(fromNode,
                createFreightDestination(fromNode, toNode,
                        timeInterval.isNoRegionCenterTransfer() ? null : toNode.getCenterRegionHierarchy()),
                timeInterval, path);
    }

    public static FreightConnection createFreightNodeConnection(Node fromNode, FreightDestination dest) {
        return new FreightConnectionImpl(fromNode, dest);
    }

    public static FreightConnectionVia createFreightNodeConnection(Node fromNode, FreightDestination dest, Transport transport) {
        return new FreightConnectionViaImpl(fromNode, dest, transport);
    }

    public static FreightConnectionVia createFreightNodeConnection(FreightConnection conn, Transport transport) {
        return new FreightConnectionViaImpl(conn.getFrom(), conn.getTo(), transport);
    }

    public static FreightConnection createFreightNodeConnection(NodeConnection nodeConnection) {
        return new FreightConnectionImpl(nodeConnection.getFrom(),
                createFreightDestination(nodeConnection.getFrom(),
                        nodeConnection.getTo(),
                        nodeConnection.getTo().getCenterRegionHierarchy()));
    }

    public static FreightDestination createFreightDestination(Node fromNode, Node toNode, RegionHierarchy toRegions) {
        return createFreightDestincationImpl(fromNode, toNode, toRegions);
    }

    public static FreightDestination createFreightDestination(Node fromNode, RegionHierarchy toRegions) {
        return createFreightDestincationImpl(fromNode, null, toRegions);
    }

    private static FreightDestination createFreightDestincationImpl(Node fromNode, Node toNode, RegionHierarchy toRegions) {
        Set<FreightColor> colors = toNode != null ? FreightAnalyser.getNodeFreightColors(fromNode, toNode) : null;
        // TODO add freight colors - based on center...
        Set<Region> regions = toRegions != null && !toRegions.getRegions().isEmpty()
                ? FreightAnalyser.transformToRegions(fromNode.getRegionHierarchy(), toRegions) : null;
        if (regions != null && !regions.isEmpty()) {
            // TODO get all colors based on all subregions
        }
        return new FreightDestinationImpl(toNode, regions, colors);
    }

    private static class FreightConnectionPathImpl extends FreightConnectionImpl implements FreightConnectionPath {

        private final TimeInterval timeInterval;
        private final List<TimeInterval> path;

        public FreightConnectionPathImpl(Node from, FreightDestination to, TimeInterval timeInterval,
                List<TimeInterval> path) {
            super(from, to);
            this.timeInterval = timeInterval;
            this.path = path;
        }

        @Override
        public TimeInterval getTimeInterval() {
            return timeInterval;
        }

        @Override
        public List<TimeInterval> getPath() {
            return path;
        }

        @Override
        public String toString() {
            return getTo().toString();
        }
    }

    private static class FreightConnectionViaImpl extends FreightConnectionImpl implements FreightConnectionVia {

        private final Transport transport;

        public FreightConnectionViaImpl(Node from, FreightDestination to, Transport transport) {
            super(from, to);
            this.transport = transport;
        }

        @Override
        public Transport getTransport() {
            return transport;
        }
    }
}
