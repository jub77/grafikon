package net.parostroj.timetable.output2.impl;

import java.util.*;
import net.parostroj.timetable.model.*;

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
    public List<Route> getRoutesForTrains(Collection<Train> trains) {
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
     * returns list of routes for lines.
     *
     * @param lines collection of lines
     * @return list of routes
     */
    public List<Route> getRoutesForLines(Collection<Line> lines) {
        Set<Route> routes = null;
        for (Line line : lines) {
            Route route = routeMap.get(line);
            if (route != null) {
                if (routes == null)
                    routes = new HashSet<Route>();
                routes.add(route);
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
    public List<NetPartRouteInfo> getRouteInfosForTrains(Collection<Train> trains) {
        List<Route> routes = this.getRoutesForTrains(trains);
        return convert(routes);
    }

    /**
     * returns list of infos about routes for the lines.
     *
     * @param trains collection of lines
     * @return list of routes
     */
    public List<NetPartRouteInfo> getRouteInfosForLines(Collection<Line> lines) {
        List<Route> routes = this.getRoutesForLines(lines);
        return convert(routes);
    }

    /**
     * returns lines for cycle.
     *
     * @param cycle trains cycle
     * @return set of lines
     */
    public Set<Line> getLinesForCycle(TrainsCycle cycle) {
        Set<Line> lines = new HashSet<Line>();
        for (TrainsCycleItem item : cycle.getItems()) {
            for (TimeInterval i : item.getIntervals()) {
                if (i.isLineOwner())
                    lines.add(i.getOwnerAsLine());
            }
        }
        return lines;
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
            // get ratio ...
            TrainDiagram diagram = ((Node) routes.iterator().next().getSegments().get(0)).getTrainDiagram();
            Double ratio = (Double) diagram.getAttribute("route.length.ratio");
            if (ratio == null)
                ratio = 1.0;
            for (Route route : routes) {
                NetPartRouteInfo info = new NetPartRouteInfo();
                info.setName(route.getName());
                long length = 0;
                for (RouteSegment seg : route.getSegments()) {
                    if (seg.isNode()) {
                        Node node = seg.asNode();
                        RouteSegmentInfo rsInfo = new RouteSegmentInfo();
                        rsInfo.setName(node.getName());
                        rsInfo.setType(node.getType().getKey());
                        rsInfo.setDistance(ratio * length);
                        info.getSegments().add(rsInfo);
                    } else {
                        Line line = seg.asLine();
                        length += line.getLength();
                    }
                }
                infos.add(info);
            }
            return infos;
        } else {
            return Collections.emptyList();
        }
    }
}
