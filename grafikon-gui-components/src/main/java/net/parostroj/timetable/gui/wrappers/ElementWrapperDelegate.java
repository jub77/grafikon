package net.parostroj.timetable.gui.wrappers;

import java.util.List;

import net.parostroj.timetable.model.*;

/**
 * Wrapper delegate for all base elements of train diagram.
 *
 * @author jub
 */
public class ElementWrapperDelegate extends BasicWrapperDelegate {
    
    private static final int ROUTE_LENGTH = 50;

    @Override
    public String toString(Object element) {
        if (element instanceof EngineClass)
            return ((EngineClass) element).getName();
        else if (element instanceof LineClass)
            return ((LineClass) element).getName();
        else if (element instanceof Node)
            return ((Node) element).getName();
        else if (element instanceof TrainsCycle)
            return ((TrainsCycle) element).getName();
        else if (element instanceof TrainType)
            return ((TrainType) element).getDesc();
        else if (element instanceof TrainTypeCategory)
            return ((TrainTypeCategory) element).getName();
        else if (element instanceof TimeInterval)
            return ((TimeInterval) element).getOwner().toString();
        else if (element instanceof TimetableImage)
            return element.toString();
        else if (element instanceof TextItem) {
            TextItem item = (TextItem) element;
            return new StringBuilder(item.getName()).append(" (").append(item.getType()).append(')').toString();
        } else if (element instanceof Route)
            return toStringRoute((Route) element);
        else if (element instanceof Train)
            return ((Train) element).getName();
        else if (element instanceof OutputTemplate)
            return ((OutputTemplate)element).getName();
        else if (element instanceof TrainsCycleType)
            return ((TrainsCycleType)element).getDescriptionText();
        else
            return super.toString(element);
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
                if (builder.length() + lastItemSize > ROUTE_LENGTH) {
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
