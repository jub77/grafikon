package net.parostroj.timetable.actions;

import java.util.Collection;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Pair;

/**
 * @author jub
 */
public final class RouteHelper {

    /**
     * Returns route which covers the most intervals for given train.
     *
     * @param train
     * @return
     */
    public static Route getBestRouteMatch(Collection<Route> routes, Train train) {
        Pair<Route, Integer> selected = null;
        for (Route route : routes) {
            int cnt = 0;
            for (RouteSegment segment : route) {
                boolean containTrain = false;
                for (TimeInterval interval : segment.getTimeIntervals()) {
                    if (interval.getTrain() == train) {
                        containTrain = true;
                        break;
                    }
                }
                if (containTrain) {
                    cnt++;
                }
            }
            if (cnt > 0 && (selected == null || selected.second < cnt)) {
                selected = new Pair<Route, Integer>(route, cnt);
            }
        }
        return selected != null ? selected.first : null;
    }
}
