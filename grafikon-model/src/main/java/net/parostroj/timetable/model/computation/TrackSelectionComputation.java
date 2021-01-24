package net.parostroj.timetable.model.computation;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.parostroj.timetable.model.Line;
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

    private final TrackConnectionComputation tcc;

    public TrackSelectionComputation() {
        tcc = new TrackConnectionComputation();
    }

    /**
     * Select line track.
     *
     * @param interval interval for the line
     * @param preselectedTrack preselected track
     * @param fromTrack from track
     * @param restrictedTracks available line tracks
     * @return selected track
     */
    public LineTrack selectLineTrack(TimeInterval interval, LineTrack preselectedTrack, NodeTrack fromTrack,
            Collection<? extends Track> restrictedTracks) {
        Line line = interval.getOwnerAsLine();
        List<LineTrack> toTracks = line.getTracks().stream()
                .filter(lt -> fromTrack == null
                        || tcc.getConnectedLineTracks(Collections.singletonList(fromTrack), line).contains(lt))
                .filter(restrictedTracks::contains)
                .collect(Collectors.toList());
        LineTrack selectedTrack = this.checkLineSelection(preselectedTrack, interval);
        if (selectedTrack != null && !toTracks.contains(selectedTrack)) {
            selectedTrack = null;
        }
        if (selectedTrack == null && fromTrack != null) {
            // check straight
            Node node = fromTrack.getOwner();
            selectedTrack = node.getConnectors().getForLine(line).stream()
                    .filter(c -> c.getStraightNodeTrack().orElse(null) == fromTrack)
                    .map(TrackConnector::getLineTrack)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(t -> this.checkLineSelection(t, interval) != null)
                    .filter(toTracks::contains)
                    .findAny()
                    .orElse(null);
        }
        if (selectedTrack == null) {
            // check which track is free for adding
            List<LineTrack> lineTracks = interval.getDirection() == TimeIntervalDirection.FORWARD
                    ? toTracks
                    : Lists.reverse(toTracks);
            for (LineTrack lineTrack : lineTracks) {
                selectedTrack = this.checkLineSelection(lineTrack, interval);
                if (selectedTrack != null) {
                    break;
                }
            }
        }
        if (selectedTrack == null) {
            // set first one
            selectedTrack = toTracks.get(interval.getDirection() == TimeIntervalDirection.FORWARD ? 0
                    : toTracks.size() - 1);
        }
        return selectedTrack;
    }

    /**
     * Select node track.
     *
     * @param interval interval for the node
     * @param preselectedTrack preselected track
     * @param fromTrack from track
     * @param restrictedTracks to node tracks
     * @return selected track
     */
    public NodeTrack selectNodeTrack(TimeInterval interval, NodeTrack preselectedTrack, LineTrack fromTrack,
            Collection<? extends Track> restrictedTracks) {
        Node node = interval.getOwnerAsNode();
        List<NodeTrack> toTracks = node.getTracks().stream()
                .filter(nt -> fromTrack == null
                        || tcc.getConnectedNodeTracks(Collections.singletonList(fromTrack), node).contains(nt))
                .filter(restrictedTracks::contains)
                .collect(Collectors.toList());
        NodeTrack selectedTrack = this.checkNodeSelection(preselectedTrack, interval);
        if (selectedTrack != null && !toTracks.contains(selectedTrack)) {
            selectedTrack = null;
        }
        if (selectedTrack == null && !interval.isFirst()) {
            // prefer straight
            selectedTrack = node.getConnectors().getForLineTrack(fromTrack)
                    .flatMap(TrackConnector::getStraightNodeTrack)
                    .filter(t -> this.checkNodeSelection(t, interval) != null)
                    .filter(toTracks::contains)
                    .orElse(null);
        }
        if (selectedTrack == null) {
            for (NodeTrack nodeTrack : toTracks) {
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
            selectedTrack = toTracks.get(0);
        }
        return selectedTrack;
    }

    /**
     * Selects track.
     *
     * @param interval interval
     * @param preselectedTrack  preselected track
     * @param fromTrack from track
     * @param restrictedTracks to track
     * @return selected track
     */
    public Track selectTrack(TimeInterval interval, Track preselectedTrack, Track fromTrack,
            Collection<? extends Track> restrictedTracks) {
        Track selectedTrack;
        if (interval.isNodeOwner()) {
            selectedTrack = this.selectNodeTrack(interval, (NodeTrack) preselectedTrack,
                    (LineTrack) fromTrack, restrictedTracks);
        } else {
            selectedTrack = this.selectLineTrack(interval, (LineTrack) preselectedTrack,
                    (NodeTrack) fromTrack, restrictedTracks);
        }
        return selectedTrack;
    }

    private LineTrack checkLineSelection(LineTrack track, TimeInterval interval) {
        return track != null && track.isFreeInterval(interval) ? track : null;
    }

    private NodeTrack checkNodeSelection(NodeTrack track, TimeInterval interval) {
        return track != null && track.isFreeInterval(interval) ? track : null;
    }
}
