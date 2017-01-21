package net.parostroj.timetable.model.freight;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.RegionHierarchy;
import net.parostroj.timetable.model.RegionHierarchyImpl;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Factory for creating destinations.
 *
 * @author jub
 */
public class FreightFactory {

    public static FreightConnectionPath createFreightNodeConnection(Node fromNode, Node toNode,
            TimeInterval timeInterval, List<TimeInterval> path) {
        return new FreightConnectionPathImpl(fromNode, createFreightDestination(toNode, timeInterval.isNoRegionCenterTransfer()), timeInterval, path);
    }

    public static FreightConnection createFreightNodeConnection(Node fromNode, Node toNode) {
        return new FreightConnectionImpl(fromNode, createFreightDestination(toNode));
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
        return new FreightConnectionImpl(nodeConnection.getFrom(), createFreightDestination(nodeConnection.getTo()));
    }

    public static FreightDestination createFreightDestination(Node node) {
        return new FreightDestinationImpl(node, null);
    }

    public static FreightDestination createFreightDestination(Node node, boolean disableCenter) {
        return new FreightDestinationImpl(node, disableCenter ? new RegionHierarchyImpl() {
            @Override
            public Set<Region> getRegions() {
                return Collections.emptySet();
            }
        } : null);
    }

    public static FreightDestination createFreightDestination(RegionHierarchy regions) {
        return new FreightDestinationImpl(null, regions);
    }

    private static class FreightDestinationImpl implements FreightDestination {

        private final Node node;
        private final RegionHierarchy regions;

        public FreightDestinationImpl(Node node, RegionHierarchy regions) {
            this.node = node;
            this.regions = regions;
        }

        @Override
        public RegionHierarchy getRegionHierarchy() {
            return regions == null ? node.getCenterRegionHierarchy() : regions;
        }

        @Override
        public Node getNode() {
            return node;
        }

        @Override
        public String toString() {
            return node.getName();
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, regions);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FreightDestinationImpl other = (FreightDestinationImpl) obj;
            return Objects.equals(node, other.node) && Objects.equals(regions, other.regions);
        }
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
