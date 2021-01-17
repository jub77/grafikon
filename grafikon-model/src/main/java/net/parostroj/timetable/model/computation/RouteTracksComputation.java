package net.parostroj.timetable.model.computation;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TimeIntervalDirection;
import net.parostroj.timetable.model.Track;

import static java.util.stream.Collectors.toList;

/**
 * @author jub
 */
public interface RouteTracksComputation {

    /**
     * Returns available node tracks which connect from and to line tracks.
     * When no source tracks are specified - the first node, in case of no
     * destination tracks - the last node.
     *
     * @param fromTracks source tracks
     * @param node       node
     * @param toTracks   destination tracks
     * @return available node tracks
     */
    Set<NodeTrack> getAvailableNodeTracks(Collection<? extends Track> fromTracks, Node node,
                                          Collection<? extends Track> toTracks);

    /**
     * Returns available line tracks which connect from and to node tracks.
     *
     * @param fromTracks source tracks
     * @param line       line
     * @param direction  direction of movement
     * @param toTracks   destination tracks
     * @return available line tracks
     */
    Set<LineTrack> getAvailableLineTracks(Collection<? extends Track> fromTracks, Line line,
                                          TimeIntervalDirection direction, Collection<? extends Track> toTracks);

    /**
     * Returns list of available node tracks. The line before and after has to
     * have already assigned tracks (first and last train interval is exception).
     *
     * @param nodeInterval node interval
     * @return list of available node tracks (previous interval has to have assigned
     * track)
     */
    List<NodeTrack> getAvailableNodeTracks(TimeInterval nodeInterval);

    /**
     * Returns list of available line tracks. The node before and after has to
     * have already assigned tracks.
     *
     * @param lineInterval line interval
     * @return list of available line tracks (previous interval has to have assigned
     * track)
     */
    List<LineTrack> getAvailableLineTracks(TimeInterval lineInterval);

    default List<? extends Track> getAvailableTracks(TimeInterval interval) {
        return interval.isNodeOwner() ? this.getAvailableNodeTracks(interval)
                : this.getAvailableLineTracks(interval);
    }

    default <T extends Track> List<T> toTrackList(TimeInterval interval,
                                                  Collection<? extends Track> tracks, Class<T> cls) {
        Stream<T> stream = interval.getOwner().getTracks().stream().filter(tracks::contains)
                .map(cls::cast);
        return stream.collect(toList());
    }

    static RouteTracksComputation getDefaultInstance() {
        return DefaultRouteTracksComputation.INSTANCE;
    }
}
