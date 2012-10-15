package net.parostroj.timetable.utils;

import java.util.List;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Route;

/**
 * Util methods.
 *
 * @author jub
 */
public class CheckingUtils {
    /**
     * checks if the specified node is not a part of any route.
     *
     * @param node checked node
     * @param routes list of routes
     * @return if is a part
     */
    public static boolean checkRoutesForNode(Node node, List<Route> routes) {
        for (Route route : routes) {
            if (route.contains(node))
                return true;
        }
        return false;
    }

    /**
     * checks if the specified line is not part of any route.
     *
     * @param line checked line
     * @param routes list of routes
     * @return if is a part
     */
    public static boolean checkRoutesForLine(Line line, List<Route> routes) {
        for (Route route : routes) {
            if (route.contains(line))
                return true;
        }
        return false;
    }
}
