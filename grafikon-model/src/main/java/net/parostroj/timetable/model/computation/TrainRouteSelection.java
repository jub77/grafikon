package net.parostroj.timetable.model.computation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Track;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.Train;

/**
 * Selection of route of a train utilities.
 */
public class TrainRouteSelection {

    private final TrackSelectionComputation trackSelection;
    private final TrainRouteTracksComputation tracksComputation;

    public TrainRouteSelection() {
        trackSelection = new TrackSelectionComputation();
        tracksComputation = new TrainRouteTracksComputation();
    }

    /**
     * Changes track - in case of different track route (change the whole chain).
     *
     * @param train train
     * @param interval interval
     * @param newTrack requested track
     */
    public void changeTrack(Train train, TimeInterval interval, Track newTrack) {
        if (interval.getOwner() != newTrack.getOwner()) {
            throw new IllegalArgumentException("Invalid track owner");
        }
        if (isTrackValid(interval, newTrack)) {
            // change directly
            interval.setTrack(newTrack);
        } else {
            // check alternative routes
            Map<TimeInterval, Set<Track>> map = new HashMap<>();
            if (!interval.isFirst()) {
                // first part
                TimeInterval firstInterval = train.getFirstInterval();
                List<TimeInterval> intervals = train.getIntervals(firstInterval, interval);
                Map<TimeInterval, Set<Track>> trackMap = tracksComputation.getAvailableTracksForIntervals(
                        intervals, firstInterval.getOwnerAsNode().getTracks(),
                        Collections.singletonList(newTrack));
                map.putAll(trackMap);
            }
            if (!interval.isLast()) {
                // second part
                TimeInterval lastInterval = train.getLastInterval();
                List<TimeInterval> intervals = train.getIntervals(interval, lastInterval);
                Map<TimeInterval, Set<Track>> trackMap = tracksComputation.getAvailableTracksForIntervals(
                        intervals, Collections.singletonList(newTrack),
                        lastInterval.getOwnerAsNode().getTracks());
                map.putAll(trackMap);
            }
            if (map.isEmpty()) {
                throw new GrafikonException("No route");
            }
            Track fromTrack = null;
            for (TimeInterval checkedInterval : train.getTimeIntervalList()) {
                checkedInterval.setTrack(trackSelection.selectTrack(checkedInterval, checkedInterval.getTrack(),
                        fromTrack, map.get(checkedInterval)));
                fromTrack = checkedInterval.getTrack();
            }
        }
    }

    /**
     * Checks if the new track of the interval is valid.
     *
     * @param interval time interval
     * @return if the new track is connected to existing one before and after
     */
    public boolean isTrackValid(TimeInterval interval, Track newTrack) {
        boolean isValid = true;
        if (!interval.isFirst()) {
            Track fromTrack = interval.getPreviousTrainInterval().getTrack();
            isValid = isConnection(fromTrack, newTrack);
        }
        if (!interval.isLast() && isValid) {
            Track toTrack = interval.getNextTrainInterval().getTrack();
            isValid = isConnection(newTrack, toTrack);
        }
        return isValid;
    }

    /**
     * Checks if the tracks of the train are valid (connection exists between tracks).
     *
     * @param train train
     * @return true if the train has valid track connections
     */
    public boolean isTrainTracksValid(Train train) {
        return getFirstTrainInvalidTrack(train).isEmpty();
    }

    /**
     * Returns first time interval with invalid track.
     *
     * @param train train
     * @return time interval with invalid track otherwise empty
     */
    public Optional<TimeInterval> getFirstTrainInvalidTrack(Train train) {
        TimeInterval lastInterval = null;
        Optional<TimeInterval> invalidTrack = Optional.empty();
        for (TimeInterval interval : train.getTimeIntervalList()) {
            if (lastInterval != null) {
                if (!isConnection(lastInterval.getTrack(), interval.getTrack())) {
                    invalidTrack = Optional.of(interval);
                    break;
                }
            }
            lastInterval = interval;
        }
        return invalidTrack;
    }

    private boolean isConnection(Track fromTrack, Track toTrack) {
        boolean isConnection = false;
        if (fromTrack.getOwner() instanceof Node fromNode) {
            Optional<TrackConnector> connector = fromNode.getConnectors().getForLineTrack((LineTrack) toTrack);
            isConnection = connector.filter(c -> c.getSwitches().containsNodeTrack((NodeTrack) fromTrack)).isPresent();
        }
        if (toTrack.getOwner() instanceof Node toNode) {
            Optional<TrackConnector> connector = toNode.getConnectors().getForLineTrack((LineTrack) fromTrack);
            isConnection = connector.filter(c -> c.getSwitches().containsNodeTrack((NodeTrack) toTrack)).isPresent();
        }
        return isConnection;
    }
}
