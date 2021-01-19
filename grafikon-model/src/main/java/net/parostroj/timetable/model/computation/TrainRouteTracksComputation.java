package net.parostroj.timetable.model.computation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

    private final TrackConnectionComputation connectionComp;

    public TrainRouteTracksComputation() {
        this.connectionComp = new TrackConnectionComputation();
    }

    /**
     * Returns map with possible tracks for the route. In case no existing route the
     * method return empty map.
     *
     * @param train train
     * @return map with available tracks for the route of the train
     */
    public Map<TimeInterval, Set<Track>> getAvailableTracksForTrain(Train train) {
        return this.get(train.getTimeIntervalList(),
                train.getFirstInterval().getOwner().getTracks(),
                train.getLastInterval().getOwner().getTracks());
    }

    public Map<TimeInterval, Set<Track>> getAvailableTracksForIntervals(List<TimeInterval> invervals,
            Collection<? extends Track> firstTracks,
            Collection<? extends Track> lastTracks) {
        return this.get(invervals, firstTracks, lastTracks);
    }

    private Map<TimeInterval, Set<Track>> get(List<TimeInterval> intervals,
            Collection<? extends Track> fromTracks,
            Collection<? extends Track> toTracks) {
        boolean isPath = false;
        TimeInterval first = intervals.get(0);
        Context context = new Context(toTracks, intervals);
        for (Track fromTrack : fromTracks) {
            boolean is = this.isPath(first, fromTrack, context);
            if (is) {
                context.result.get(first).add(fromTrack);
            }
            isPath = isPath || is;
        }
        return isPath ? context.result : Collections.emptyMap();
    }

    private boolean isPath(TimeInterval fromInterval, Track fromTrack, Context context) {
        TimeInterval toInterval = context.getNextInterval(fromInterval);
        // already checked
        if (context.containsTrack(fromInterval, fromTrack)) {
            return true;
        }
        boolean isPath = false;
        if (toInterval == null) {
            // no toInterval -> check with last tracks
            isPath = context.addLastTrack(fromInterval, fromTrack);
        } else {
            // recursive check for next interval
            Set<? extends Track> tracks = getToTracks(fromTrack, toInterval);

            for (Track track : tracks) {
                boolean is = this.isPath(toInterval, track, context);
                if (is) {
                    context.result.get(toInterval).add(track);
                }
                isPath = isPath || is;
            }
        }

        return isPath;
    }

    private Set<? extends Track> getToTracks(Track fromTrack, TimeInterval toInterval) {
        Set<? extends Track> tracks;
        if (toInterval.isNodeOwner()) {
            tracks = connectionComp.getConnectedNodeTracks(Collections.singleton(fromTrack),
                    toInterval.getOwnerAsNode());
        } else {
            tracks = connectionComp.getConnectedLineTracks(Collections.singleton(fromTrack),
                    toInterval.getOwnerAsLine());
        }
        return tracks;
    }

    private static final class Context {
        final Collection<? extends Track> lastTracks;
        final List<TimeInterval> intervals;
        final Map<TimeInterval, Set<Track>> result;

        Context(Collection<? extends Track> lastTracks, List<TimeInterval> intervals) {
            this.lastTracks = lastTracks;
            this.intervals = intervals;
            this.result = new HashMap<>();
        }

        TimeInterval getNextInterval(TimeInterval interval) {
            int index = intervals.indexOf(interval);
            return index + 1 >= intervals.size() ? null : intervals.get(index + 1);
        }

        boolean containsTrack(TimeInterval interval, Track track) {
            Set<Track> tracks = result.computeIfAbsent(interval, k -> new HashSet<>());
            return tracks.contains(track);
        }

        void addTrack(TimeInterval interval, Track track) {
            result.get(interval).add(track);
        }

        boolean addLastTrack(TimeInterval interval, Track track) {
            if (lastTracks.contains(track)) {
                this.addTrack(interval, track);
                return true;
            } else {
                return false;
            }
        }
    }
}
