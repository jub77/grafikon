package net.parostroj.timetable.utils;

import java.util.List;
import java.util.ListIterator;

import net.parostroj.timetable.model.*;

import com.google.common.base.Joiner;

/**
 * Transformation of texts.
 *
 * @author jub
 */
public class TransformUtil {

    public static final String THREE_DOTS = "...";
    public static final String SEPARATOR = ",";
    public static final String ROUTE_FORMAT = "%s [%s]";
    public static final String ROUTE_FORMAT_NET_PART = "%s * [%s]";
    public static final String ROUTE_FORMAT_NO_NAME = "[%2$3]";

    public static String getRouteFormat(Route route, boolean withNetIdentification) {
        String format = withNetIdentification && route.isNetPart() ? ROUTE_FORMAT_NET_PART : ROUTE_FORMAT;
        if (ObjectsUtil.isEmpty(route.getName())) {
            format = ROUTE_FORMAT_NO_NAME;
        }
        return format;
    }

    public static String transformRoute(Route route) {
        return transformRoute(route, getRouteFormat(route, true), 0);
    }

    public static String transformRoute(Route route, String format, int maxSegmentsLength) {
        return transformRoute(route, format, SEPARATOR, maxSegmentsLength);
    }

    public static String transformRoute(Route route, String format, String separator, int maxSegmentsLengt) {
        String name = ObjectsUtil.trimNonEmpty(route.getName());
        String segments = getRouteSegments(route, separator, maxSegmentsLengt);
        return String.format(format, name, segments).trim();
    }

    private static String getRouteSegments(Route route, String separator, int maxSegmentsLengt) {
        if (maxSegmentsLengt <= 0) {
            return Joiner.on(separator).join(route.getSegments());
        } else {
            StringBuilder builder = new StringBuilder();
            List<RouteSegment> segments = route.getSegments();
            RouteSegment lastRouteSegment = segments.get(segments.size() - 1);
            String lastItem = transformStation((Node) lastRouteSegment);
            boolean first = true;
            for (RouteSegment segment : route.getSegments()) {
                if (segment.asNode() != null) {
                    if (!first) {
                        builder.append(separator);
                    } else {
                        first = false;
                    }
                    if ((builder.length() + lastItem.length() + separator.length() + THREE_DOTS.length()) > maxSegmentsLengt) {
                        builder.append(THREE_DOTS);
                        builder.append(separator);
                        builder.append(lastRouteSegment);
                        break;
                    }
                    builder.append(segment);
                }
            }
            return builder.toString();
        }
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
