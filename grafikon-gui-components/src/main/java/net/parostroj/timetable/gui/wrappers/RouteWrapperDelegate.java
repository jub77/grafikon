package net.parostroj.timetable.gui.wrappers;

import java.util.List;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.RouteSegment;

/**
 * Delegate for trains.
 *
 * @author jub
 */
public class RouteWrapperDelegate extends BasicWrapperDelegate {

    private static final int ROUTE_LENGTH = 50;

    public enum Type {
        SHORT, FULL;
    }

    private final Type type;

    public RouteWrapperDelegate(Type type) {
        this.type = type;
    }

    @Override
    protected String toCompareString(Object element) {
        String result = ((Route) element).getName();
        if (result == null) {
            result = ((Route) element).toString();
        }
        return result;
    }

    @Override
    public String toString(Object element) {
        return toStringRoute((Route) element);
    }

    private String toStringRoute(Route route) {
        StringBuilder builder = new StringBuilder();
        // name
        if (route.getName() != null && !"".equals(route.getName())) {
            builder.append(route.getName()).append(' ');
        }
        // net part
        if (route.isNetPart()) {
            builder.append("* ");
        }
        // nodes
        builder.append('[');
        List<RouteSegment> segments = route.getSegments();
        RouteSegment lastRouteSegment = segments.get(segments.size() - 1);
        int lastItemSize = lastRouteSegment.toString().length() + 1;
        boolean first = true;
        for (RouteSegment segment : route.getSegments()) {
            if (segment.asNode() != null) {
                if (!first) {
                    builder.append(',');
                } else {
                    first = false;
                }
                if (type == Type.SHORT && (builder.length() + lastItemSize) > ROUTE_LENGTH) {
                    builder.append("...,");
                    builder.append(lastRouteSegment);
                    break;
                }
                builder.append(segment);
            }
        }
        builder.append(']');
        return builder.toString();
    }
}
