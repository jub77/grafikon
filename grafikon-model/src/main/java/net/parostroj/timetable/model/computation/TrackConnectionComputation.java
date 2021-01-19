package net.parostroj.timetable.model.computation;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeIntervalDirection;
import net.parostroj.timetable.model.Track;
import net.parostroj.timetable.model.TrackConnectorSwitch;

import static java.util.stream.Collectors.toSet;

/**
 * Computation of connection between line and node tracks.
 */
public class TrackConnectionComputation {
    /**
     * Returns line tracks which are reachable from specified node tracks.
     *
     * @param fromTracks collection of node tracks
     * @param line line to be reached
     * @param direction direction of line
     * @return set of line tracks
     */
    public Set<LineTrack> getConnectedLineTracks(Collection<? extends Track> fromTracks, Line line,
            TimeIntervalDirection direction) {
        return line.getFrom(direction).getConnectors().stream()
                .filter(c -> c.getLineTrack().map(LineTrack::getOwner).orElse(null) == line)
                .filter(c -> c.getSwitches().stream()
                        .anyMatch(sw -> fromTracks.contains(sw.getNodeTrack())))
                .map(c -> c.getLineTrack().get())
                .collect(toSet());
    }

    /**
     * Returns node tracks which are reachable from specified line tracks.
     *
     * @param fromTracks collection of line tracks
     * @param node node to be reached
     * @return set of node tracks
     */
    public Set<NodeTrack> getConnectedNodeTracks(Collection<? extends Track> fromTracks,
            Node node) {
        return fromTracks.stream()
                .map(lt -> node.getConnectors().getForLineTrack((LineTrack) lt))
                .filter(Optional::isPresent)
                .flatMap(c -> c.get().getSwitches().stream())
                .map(TrackConnectorSwitch::getNodeTrack)
                .collect(toSet());
    }
}
