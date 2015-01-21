package net.parostroj.timetable.output2.gt;

import java.awt.Graphics2D;

import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.utils.TransformUtil;

public class RouteStringSupplier implements LimitedStringSupplier {

    private final Route route;

    public RouteStringSupplier(Route route) {
        this.route = route;
    }

    @Override
    public String get(Graphics2D g, int width) {
        return DrawUtils.getStringForWidth(g,
                remove -> TransformUtil.transformRoute(route, TransformUtil.ROUTE_FORMAT, TransformUtil.SEPARATOR, "", 0, remove),
                width, "");
    }
}
