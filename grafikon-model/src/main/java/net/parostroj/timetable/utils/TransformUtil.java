package net.parostroj.timetable.utils;

import java.util.List;
import java.util.ListIterator;

import net.parostroj.timetable.model.*;

/**
 * Transformation of texts.
 *
 * @author jub
 */
public class TransformUtil {

    public static final String THREE_DOTS = "...";
    public static final String SEPARATOR = ",";
    public static final String NET_PART = " *";
    public static final String ROUTE_FORMAT = "%s%s [%s]";

    public static String transformRoute(Route route) {
        return transformRoute(route, ROUTE_FORMAT, 0);
    }

    public static String transformRoute(Route route, String format, int maxLength) {
        return transformRoute(route, format, SEPARATOR, NET_PART, maxLength, 0);
    }

    public static String transformRoute(Route route, String format, String separator, String netPart, int maxLength, int removedSegments) {
        String name = ObjectsUtil.trimNonEmpty(route.getName());
        if (maxLength > 0) {
            int baseLength = String.format(format, name, route.isNetPart() ? netPart : "", "").length();
            maxLength -= baseLength;
        }
        String segments = getRouteSegments(route, separator, maxLength, removedSegments);
        return String.format(format, name, route.isNetPart() ? netPart : "", segments);
    }

    public static String getRouteSegments(Route route, String separator, int maxSegmentsLength, int removedSegments) {
        StringBuilder builder = new StringBuilder();
        List<RouteSegment> segments = route.getSegments();
        RouteSegment lastRouteSegment = segments.get(segments.size() - 1);
        String lastItem = transformStation((Node) lastRouteSegment);
        boolean first = true;
        int cnt = 0;
        for (RouteSegment segment : route.getSegments()) {
            if (segment.asNode() != null) {
                cnt++;
                if (!first) {
                    builder.append(separator);
                } else {
                    first = false;
                }
                if ((maxSegmentsLength > 0 && (builder.length() + lastItem.length() + separator.length() + THREE_DOTS.length()) > maxSegmentsLength) ||
                        (removedSegments > 0 && cnt > removedSegments)) {
                    builder.append(THREE_DOTS);
                    builder.append(separator);
                    builder.append(lastItem);
                    break;
                }
                builder.append(transformStation((Node) segment));
            }
        }
        return builder.toString();
    }

    /**
     * creates name of the station.
     *
     * @param node station
     * @param stop abbreviation for stops
     * @param stopFreight abbreviation for stops with freight
     * @return transformed name
     */
    public static String transformStation(Node node, String stop, String stopFreight) {
        String name = node.getName();
        if (node.getType() == NodeType.STOP && stop != null) {
            name += " " + stop;
        } else if (node.getType() == NodeType.STOP_WITH_FREIGHT && stopFreight != null) {
            name += " " + stopFreight;
        }
        return name;
    }

    public static String transformStation(Node node) {
        return transformStation(node, null, null);
    }

    public static String getFromAbbr(TimeInterval i) {
        Node node = null;
        boolean found = false;
        Node firstNode = null;
        for (TimeInterval current : i.getTrain().getTimeIntervalList()) {
            if (current == i) {
                found = true;
                break;
            }
            if (current.isNodeOwner()) {
                Node n = current.getOwnerAsNode();
                if (firstNode == null)
                    firstNode = n;
                switch (n.getType()) {
                    case STOP: case STOP_WITH_FREIGHT: case ROUTE_SPLIT: case SIGNAL:
                        // do nothing
                        break;
                    default:
                        node = n;
                        break;
                }
            }
        }
        if (found) {
            return node != null ? node.getAbbr() : (firstNode != null ? firstNode.getAbbr() : null);
        } else {
            return null;
        }
    }

    public static String getToAbbr(TimeInterval i) {
        Node node = null;
        boolean found = false;
        List<TimeInterval> list = i.getTrain().getTimeIntervalList();
        ListIterator<TimeInterval> iterator = list.listIterator(list.size());
        Node firstNode = null;
        while (iterator.hasPrevious()) {
            TimeInterval current = iterator.previous();
            if (current == i) {
                found = true;
                break;
            }
            if (current.isNodeOwner()) {
                Node n = current.getOwnerAsNode();
                if (firstNode == null) {
                    firstNode = n;
                }
                switch (n.getType()) {
                    case STOP: case STOP_WITH_FREIGHT: case ROUTE_SPLIT: case SIGNAL:
                        // do nothing
                        break;
                    default:
                        node = n;
                        break;
                }
            }
        }
        if (found) {
            return node != null ? node.getAbbr() : (firstNode != null ? firstNode.getAbbr() : null);
        } else {
            return null;
        }
    }

    public static String getEngineCycleDescription(TrainsCycle ec) {
        if (!TrainsCycleType.ENGINE_CYCLE.equals(ec.getType().getName())) {
            throw new IllegalArgumentException("Engine cycle expected.");
        }

        String result = (ec.getDescription() != null) ? ec.getDescription().trim() : "";
        EngineClass cl = ec.getAttribute(TrainsCycle.ATTR_ENGINE_CLASS, EngineClass.class);
        if (cl != null) {
            String desc = result;
            result = cl.getName();
            if (!"".equals(desc)) {
                result = String.format("%s (%s)", result, desc);
            }
        }
        return result;
    }

    public static String getEngineDescription(TrainsCycle ec) {
        EngineClass cl = ec.getAttribute(TrainsCycle.ATTR_ENGINE_CLASS, EngineClass.class);
        if (cl != null) {
            return cl.getName();
        } else {
            return ec.getDescription();
        }
    }
}
