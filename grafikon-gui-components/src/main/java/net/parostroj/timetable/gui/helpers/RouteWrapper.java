package net.parostroj.timetable.gui.helpers;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.RouteSegment;

/**
 * Wrapper for routes.
 *
 * @author jub
 */
public class RouteWrapper extends Wrapper<Route> {

    public RouteWrapper(Route route) {
        super(route);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        // net part
        if (getElement().isNetPart()) {
            builder.append("* ");
        }
        // name
        if (getElement().getName() != null && !"".equals(getElement().getName())) {
            builder.append(getElement().getName()).append(' ');
        }
        // nodes
        builder.append('[');
        boolean first = true;
        for (RouteSegment segment : getElement().getSegments()) {
            if (segment.asNode() != null) {
                if (!first) {
                    builder.append(',');
                } else {
                    first = false;
                }
                builder.append(segment);
            }
        }
        builder.append(']');
        return builder.toString();
    }
}
