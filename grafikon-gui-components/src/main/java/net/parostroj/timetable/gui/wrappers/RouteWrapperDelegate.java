package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.utils.TransformUtil;

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
        return TransformUtil.transformRoute(route, TransformUtil.ROUTE_FORMAT, TransformUtil.SEPARATOR, "", type == Type.SHORT ? ROUTE_LENGTH : 0, 0);
    }
}
