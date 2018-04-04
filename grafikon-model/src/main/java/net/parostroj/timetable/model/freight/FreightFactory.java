package net.parostroj.timetable.model.freight;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.RegionHierarchy;

/**
 * Factory for creating destinations.
 *
 * @author jub
 */
public class FreightFactory {

    private FreightFactory() {}

    public static FreightConnectionPath createFreightNodeConnection(Node fromNode, Node toNode, boolean regionTransfer,
            Collection<? extends TrainConnection> path) {
        return new FreightConnectionPathImpl(fromNode,
                createFreightDestination(fromNode, toNode, regionTransfer ? toNode.getCenterRegionHierarchy() : null),
                createTrainPath(path));
    }

    public static FreightConnectionPath createFreightNodeConnection(Node fromNode, FreightDestination dest, TrainPath path) {
        return new FreightConnectionPathImpl(fromNode, dest, path);
    }

    public static TrainPath createTrainPath(Collection<? extends TrainConnection> collection) {
        return collection.stream().collect(toCollection(TrainPathImpl::new));
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
        return createFreightDestinationImpl(fromNode, toNode, toRegions);
    }

    public static FreightDestination createFreightDestination(Node fromNode, RegionHierarchy toRegions) {
        return createFreightDestinationImpl(fromNode, null, toRegions);
    }

    private static FreightDestination createFreightDestinationImpl(Node fromNode, Node toNode, RegionHierarchy toRegions) {
        Set<Region> regions = FreightAnalyser.transformToRegions(fromNode.getRegionHierarchy(), toRegions);
        Set<FreightColor> colors = FreightAnalyser.getFreightColors(fromNode, toNode, regions);
        return new FreightDestinationImpl(toNode, regions, colors);
    }

    private static class FreightConnectionPathImpl extends FreightConnectionImpl implements FreightConnectionPath {

        private final TrainPath path;

        public FreightConnectionPathImpl(Node from, FreightDestination to, TrainPath path) {
            super(from, to);
            this.path = path;
        }

        @Override
        public TrainPath getPath() {
            return path;
        }

        @Override
        public String toString() {
            return String.format("%s via %s", super.toString(), path);
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

        @Override
        public String toString() {
            return String.format("%s to %s via (%s)", getFrom(), getTo(), transport);
        }
    }

    private static class TrainPathImpl extends ArrayList<TrainConnection> implements TrainPath {

        private static final long serialVersionUID = 1L;
    }
}
