package net.parostroj.timetable.model.computation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Track;
import net.parostroj.timetable.model.Train;

/**
 * @author jub
 */
public class TrainRouteTracksComputation {

    private final RouteTracksComputation comp;

    public TrainRouteTracksComputation(RouteTracksComputation routeComputation) {
        this.comp = routeComputation;
    }

    /**
     * Returns map with possible tracks for the route. In case no existing route the
     * method return empty map.
     *
     * @param train train
     * @return map with available tracks for the route of the train
     */
    public Map<TimeInterval, Set<? extends Track>> getAvailableTracksForTrain(Train train) {
        Map<TimeInterval, Set<? extends Track>> result = new HashMap<>();
        List<TimeInterval> til = train.getTimeIntervalList();
        for (int i = til.size() - 1; i >= 0; i--) {
            TimeInterval interval = til.get(i);
            Set<? extends Track> tracks;
            Collection<? extends Track> from = Collections.emptyList();
            Collection<? extends Track> to = Collections.emptyList();
            if (interval.isLast()) {
                from = interval.getPreviousTrainInterval().getOwnerAsLine().getTracks();
            } else if (interval.isFirst()) {
                to = result.get(interval.getNextTrainInterval());
            } else {
                from = interval.getPreviousTrainInterval().getOwner().getTracks();
                to = result.get(interval.getNextTrainInterval());
            }
            tracks = this.getAvailableTracks(from, interval, to);
            if (tracks.isEmpty()) {
                return Collections.emptyMap();
            }
            result.put(interval, tracks);
        }
        return result;
    }

    private Set<? extends Track> getAvailableTracks(Collection<? extends Track> fromTracks,
            TimeInterval interval, Collection<? extends Track> toTracks) {
        if (interval.isNodeOwner()) {
            return comp.getAvailableNodeTracks(fromTracks, interval.getOwnerAsNode(), toTracks);
        } else {
            return comp.getAvailableLineTracks(fromTracks, interval.getOwnerAsLine(),
                    interval.getDirection(), toTracks);
        }
    }
}
