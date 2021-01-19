package net.parostroj.timetable.model.computation;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TimeIntervalDirection;
import net.parostroj.timetable.model.Track;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrainType;

/**
 * Track selection helper when creating train.
 */
public class TrackSelectionComputation {

    private final RouteTracksComputation rtc;

    public TrackSelectionComputation() {
        rtc = RouteTracksComputation.getDefaultInstance();
    }

    /**
     * Select line track.
     *
     * @param interval interval for the line
     * @param preselectedTrack preselected track
     * @param fromTrack from track
     * @param toTracks available destination tracks
     * @return selected track
     */
    public LineTrack selectLineTrack(TimeInterval interval, LineTrack preselectedTrack, NodeTrack fromTrack,
            Collection<? extends Track> toTracks) {
        LineTrack selectedTrack = this.checkLineSelection(preselectedTrack, interval);
        RouteTracksComputation rtc = RouteTracksComputation.getDefaultInstance();
        Set<LineTrack> trackSet = rtc.getAvailableLineTracks(
                Collections.singletonList(fromTrack), interval.getOwnerAsLine(),
                interval.getDirection(), toTracks);
        List<LineTrack> tracks = rtc.toTrackList(interval, trackSet, LineTrack.class);
        if (!trackSet.contains(selectedTrack)) {
            selectedTrack = null;
        }
        if (selectedTrack == null) {
            // check straight
            NodeTrack pNodeTrack = (NodeTrack) interval.getPreviousTrainInterval().getTrack();
            Node node = pNodeTrack.getOwner();
            selectedTrack = node.getConnectors().getForLine(interval.getOwnerAsLine()).stream()
                    .filter(c -> c.getStraightNodeTrack().orElse(null) == pNodeTrack)
                    .map(c -> c.getLineTrack().get())
                    .filter(t -> this.checkLineSelection(t, interval) != null)
                    .filter(trackSet::contains)
                    .findAny()
                    .orElse(null);
        }
        if (selectedTrack == null) {
            // check which track is free for adding
            List<LineTrack> lineTracks = interval.getDirection() == TimeIntervalDirection.FORWARD
                    ? tracks
                    : Lists.reverse(tracks);
            for (LineTrack lineTrack : lineTracks) {
                selectedTrack = this.checkLineSelection(lineTrack, interval);
                if (selectedTrack != null) {
                    break;
                }
            }
        }
        if (selectedTrack == null) {
            // set first one
            selectedTrack = tracks.get(interval.getDirection() == TimeIntervalDirection.FORWARD ? 0
                    : tracks.size() - 1);
        }
        return selectedTrack;
    }

    private LineTrack checkLineSelection(LineTrack track, TimeInterval interval) {
        return track != null && track.isFreeInterval(interval) ? track : null;
    }

    public NodeTrack selectNodeTrack(TimeInterval interval, NodeTrack preselectedTrack, LineTrack fromTrack,
            Collection<? extends Track> toTracks) {
        NodeTrack selectedTrack = this.checkNodeSelection(preselectedTrack, interval);
        Node node = interval.getOwnerAsNode();
        Set<NodeTrack> trackSet = rtc.getAvailableNodeTracks(
                fromTrack != null ? Collections.singletonList(fromTrack) : Collections.emptySet(),
                interval.getOwnerAsNode(), toTracks);
        List<NodeTrack> tracks = rtc.toTrackList(interval, trackSet, NodeTrack.class);
        if (!trackSet.contains(selectedTrack)) {
            selectedTrack = null;
        }
        if (selectedTrack == null && !interval.isFirst()) {
            // prefer straight
            LineTrack lineTrack = (LineTrack) interval.getPreviousTrainInterval().getTrack();
            selectedTrack = node.getConnectors().getForLineTrack(lineTrack)
                    .flatMap(TrackConnector::getStraightNodeTrack)
                    .filter(t -> this.checkNodeSelection(t, interval) != null)
                    .filter(trackSet::contains)
                    .orElse(null);
        }
        if (selectedTrack == null) {
            for (NodeTrack nodeTrack : tracks) {
                TrainType trainType = interval.getTrain().getType();
                if (interval.getLength() != 0 && trainType != null && trainType.isPlatform()
                        && !nodeTrack.isPlatform()) {
                    // skip station tracks with no platform (if needed)
                    continue;
                }
                selectedTrack = this.checkNodeSelection(nodeTrack, interval);
                if (selectedTrack != null) {
                    break;
                }
            }
        }
        if (selectedTrack == null) {
            // set first one
            selectedTrack = tracks.get(0);
        }
        return selectedTrack;
    }

    private NodeTrack checkNodeSelection(NodeTrack track, TimeInterval interval) {
        return track != null && track.isFreeInterval(interval) ? track : null;
    }
}
