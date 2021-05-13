package net.parostroj.timetable.actions;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Pair;

/**
 * @author jub
 */
public final class RouteHelper {

    private RouteHelper() {}

    /**
     * Returns route which covers the most intervals for given train.
     *
     * @param train train
     * @return route covering the most intervals
     */
    public static Route getBestRouteMatch(Iterable<Route> routes, Train train) {
        Pair<Route, Integer> selected = null;
        for (Route route : routes) {
            int cnt = 0;
            for (RouteSegment rSegment : route) {
                boolean containTrain = false;
                NetSegment<?> segment = (NetSegment<?>) rSegment;
                for (TimeInterval interval : segment) {
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
                selected = new Pair<>(route, cnt);
            }
        }
        return selected != null ? selected.first : null;
    }
}
