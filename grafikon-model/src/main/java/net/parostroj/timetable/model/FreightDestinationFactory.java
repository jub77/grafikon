package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Factory for creating destinations.
 *
 * @author jub
 */
public class FreightDestinationFactory {

    public static FreightDestinationWithPath createFreightDestination(Node fromNode, Node toNode, TimeInterval timeInterval, List<TimeInterval> path) {
        return new FreightDestinationWithPathNode(fromNode, toNode, timeInterval, path);
    }

    public static FreightDestination createFreightDestination(Node fromNode, Node toNode) {
        return new FreightDestinationNode(fromNode, toNode);
    }

    private static class FreightDestinationNode implements FreightDestination {

        private final Node fromNode;
        private final Node toNode;

        public FreightDestinationNode(Node fromNode, Node toNode) {
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        @Override
        public Node getFrom() {
            return fromNode;
        }

        @Override
        public Node getTo() {
            return toNode;
        }

        @Override
        public String toString() {
            return toNode.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((toNode == null) ? 0 : toNode.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            FreightDestination other = (FreightDestination) obj;
            if (toNode == null) {
                if (other.getTo() != null)
                    return false;
            } else if (!toNode.equals(other.getTo()))
                return false;
            return true;
        }
    }

    private static class FreightDestinationWithPathNode extends FreightDestinationNode implements FreightDestinationWithPath {

        private final TimeInterval timeInterval;
        private final List<TimeInterval> path;

        public FreightDestinationWithPathNode(Node fromNode, Node toNode, TimeInterval timeInterval, List<TimeInterval> path) {
            super(fromNode, toNode);
            this.timeInterval = timeInterval;
            this.path = path;
        }

        @Override
        public Set<Region> getCenterRegions() {
            return !isCenterOfRegions() ? Collections.emptySet() : super.getCenterRegions();
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
        public boolean isCenterOfRegions() {
            boolean noCenter = timeInterval != null && timeInterval.getAttributeAsBool(TimeInterval.ATTR_NO_REGION_CENTER_TRANSFER);
            return !noCenter && getTo().isCenterOfRegions();
        }

        @Override
        public String toString() {
            return getTo().toString();
        }
    }
}
