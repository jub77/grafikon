package net.parostroj.timetable.output2.util;

import java.util.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.impl.NetPartRouteInfo;

/**
 * Extracts routes from list of trains.
 *
 * @author jub
 */
public class RoutesExtractor {

    private Map<Line, Route> routeMap;

    public RoutesExtractor(TrainDiagram diagram) {
        routeMap = new HashMap<Line, Route>();
        for (Route route : diagram.getRoutes()) {
            if (route.isNetPart()) {
                for (RouteSegment seg : route.getSegments()) {
                    if (seg.asLine() != null && !routeMap.containsKey(seg.asLine())) {
                        routeMap.put(seg.asLine(), route);
                    }
                }
            }
        }
    }

    /**
     * returns list of routes for the trains.
     *
     * @param trains collection of trains
     * @return list of routes
     */
    public List<Route> getRoutes(Collection<Train> trains) {
        Set<Route> routes = null;
        // collect routes
        for (Train t : trains) {
            for (TimeInterval i : t.getTimeIntervalList()) {
                if (i.isLineOwner()) {
                    Route route = routeMap.get(i.getOwnerAsLine());
                    if (route != null) {
                        if (routes == null)
                            routes = new HashSet<Route>();
                        routes.add(route);
                    }
                }
            }
        }
        return (routes == null) ? Collections.<Route>emptyList() : new ArrayList<Route>(routes);
    }

    /**
     * returns list of infos about routes for the trains.
     *
     * @param trains collection of trains
     * @return list of routes
     */
    public List<NetPartRouteInfo> getRouteInfos(Collection<Train> trains) {
        List<Route> routes = this.getRoutes(trains);
        return convert(routes);
    }

    /**
     * converts list of routes to infos.
     *
     * @param routes collection of routes.
     * @return list of infos
     */
    public static List<NetPartRouteInfo> convert(Collection<Route> routes) {
        if (routes != null && !routes.isEmpty()) {
            List<NetPartRouteInfo> infos = new LinkedList<NetPartRouteInfo>();
            for (Route route : routes) {
                NetPartRouteInfo info = new NetPartRouteInfo();
                info.setName(route.getName());
                info.getStations().add(((Node)route.getSegments().get(0)).getName());
                info.getStations().add(((Node)route.getSegments().get(route.getSegments().size() - 1)).getName());
                infos.add(info);
            }
            return infos;
        } else {
            return Collections.emptyList();
        }
    }
}
