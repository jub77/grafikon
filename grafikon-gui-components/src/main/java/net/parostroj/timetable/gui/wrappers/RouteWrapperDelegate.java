package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.utils.TransformUtil;

/**
 * Delegate for trains.
 *
 * @author jub
 */
public class RouteWrapperDelegate extends BasicWrapperDelegate<Route> {

    private static final int ROUTE_LENGTH = 50;

    public enum Type {
        SHORT, FULL, SHORT_WITH_NET, FULL_WITH_NET;
    }

    private final Type type;

    public RouteWrapperDelegate(Type type) {
        this.type = type;
    }

    @Override
    protected String toCompareString(Route element) {
        String result = element.getName();
        if (result == null) {
            result = element.toString();
        }
        return result;
    }

    @Override
    public String toString(Route element) {
        return toStringRoute(element);
    }

    private String toStringRoute(Route route) {
        String netPart = type == Type.SHORT_WITH_NET || type == Type.FULL_WITH_NET ?
                TransformUtil.NET_PART : "";
        return TransformUtil.transformRoute(route,
                TransformUtil.ROUTE_FORMAT,
                TransformUtil.SEPARATOR,
                netPart,
                type == Type.SHORT || type == Type.SHORT_WITH_NET ? ROUTE_LENGTH : 0,
                0);
    }
}
