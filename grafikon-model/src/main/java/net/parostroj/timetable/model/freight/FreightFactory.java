package net.parostroj.timetable.model.freight;

import java.util.List;
import java.util.Set;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
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

    public static FreightDestination createFreightDestination(Node fromNode, FreightDestination destination) {
        if (destination instanceof FreightDestinationFromWrapper) {
            FreightDestinationFromWrapper wrapper = (FreightDestinationFromWrapper) destination;
            if (wrapper.getFrom() == fromNode) {
                return wrapper;
            } else {
                return new FreightDestinationFromWrapper(fromNode, wrapper.getDestination());
            }
        } else {
            return new FreightDestinationFromWrapper(fromNode, destination);
        }
    }

    public static FreightDestination createFreightDestination(Node node) {
        return new FreightDestinationImpl(node, false);
    }

    public static FreightDestination createFreightDestination(Node node, boolean disableCenter) {
        return new FreightDestinationImpl(node, !disableCenter);
    }

    public static FreightDestination createFreightDestination(Set<Region> regions) {
        return new FreightDestinationImpl(regions);
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
