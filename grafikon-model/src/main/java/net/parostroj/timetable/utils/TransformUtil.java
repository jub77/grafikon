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
        return transformRoute(route, format, SEPARATOR, "", maxLength, 0);
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
        String lastNodeText = transformStation(route.getLast());
        int len = route.getNodesCount();
        if (removedSegments > 0 && len > 2) {
            getRouteSegmentsRemoved(route, separator, removedSegments, builder, lastNodeText, len);
        } else if (maxSegmentsLength > 0 && len > 2) {
            getRouteSegmentsMax(route, separator, maxSegmentsLength, builder, lastNodeText, len);
        } else {
            for (Node node : route.getNodes()) {
                addText(builder, separator, transformStation(node));
            }
        }
        return builder.toString();
    }

    private static void getRouteSegmentsMax(Route route, String separator, int maxSegmentsLength, StringBuilder builder,
            String lastNodeText, int len) {
        String previousNodeText = null;
        Node lastNode = route.getLast();
        int lastLength = lastNodeText.length() + separator.length();
        int threeDotsLength = THREE_DOTS.length() + separator.length();
        for (Node node : route.getNodes()) {
            int previousLength = previousNodeText != null ? previousNodeText.length() + separator.length() : 0;
            if (maxSegmentsLength > 0
                    && (builder.length() + previousLength + lastLength + threeDotsLength) > maxSegmentsLength
                    && !(node == lastNode && (builder.length() + previousLength + lastLength <= maxSegmentsLength))) {
                if (builder.length() == 0) {
                    addText(builder, separator, transformStation(route.getFirst()));
                }
                if (len > 2) {
                    addText(builder, separator, THREE_DOTS);
                }
                addText(builder, separator, lastNodeText);
                previousNodeText = null;
                break;
            }
            addText(builder, separator, previousNodeText);
            previousNodeText = transformStation(node);
        }
        addText(builder, separator, previousNodeText);
    }

    private static void getRouteSegmentsRemoved(Route route, String separator, int removedSegments, StringBuilder builder,
            String lastNodeText, int len) {
        int cnt = len;
        for (Node node : route.getNodes()) {
            cnt--;
            if (removedSegments > 0 && cnt <= removedSegments) {
                if (builder.length() == 0) {
                    addText(builder, separator, transformStation(route.getFirst()));
                }
                addText(builder, separator, THREE_DOTS);
                addText(builder, separator, lastNodeText);
                break;
            }
            addText(builder, separator, transformStation(node));
        }
    }

    private static void addText(StringBuilder builder, String separator, String str) {
        if (str != null) {
            if (builder.length() != 0) {
                builder.append(separator);
            }
            builder.append(str);
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

        String result = ObjectsUtil.trimNonEmpty(ec.getDescription());
        EngineClass cl = ec.getAttribute(TrainsCycle.ATTR_ENGINE_CLASS, EngineClass.class);
        if (cl != null) {
            String desc = result;
            result = cl.getName();
            if (!ObjectsUtil.isEmpty(desc)) {
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
