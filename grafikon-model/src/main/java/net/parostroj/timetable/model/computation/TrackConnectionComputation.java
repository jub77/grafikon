package net.parostroj.timetable.model.computation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
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
     * @return set of line tracks
     */
    public Set<LineTrack> getConnectedLineTracks(Collection<? extends Track> fromTracks, Line line) {
        return Stream.of(line.getFrom(), line.getTo()).flatMap(n -> n.getConnectors().stream())
                .filter(c -> c.getLineTrack().map(LineTrack::getOwner).orElse(null) == line)
                .filter(c -> c.getSwitches().stream()
                        .anyMatch(sw -> fromTracks.contains(sw.getNodeTrack())))
                .map(c -> c.getLineTrack().get())
                .collect(toSet());
    }

    public List<LineTrack> getConnectedLineTracksList(Collection<? extends Track> fromTracks, Line line) {
        return sortTracks(line.getTracks(), getConnectedLineTracks(fromTracks, line));
    }

    /**
     * Returns node tracks which are reachable from specified line tracks.
     *
     * @param fromTracks collection of line tracks
     * @param node node to be reached
     * @return set of node tracks
     */
    public Set<NodeTrack> getConnectedNodeTracks(Collection<? extends Track> fromTracks, Node node) {
        return fromTracks.stream()
                .map(lt -> node.getConnectors().getForLineTrack((LineTrack) lt))
                .filter(Optional::isPresent)
                .flatMap(c -> c.get().getSwitches().stream())
                .map(TrackConnectorSwitch::getNodeTrack)
                .collect(toSet());
    }

    public List<NodeTrack> getConnectedNodeTracksList(Collection<? extends Track> fromTracks, Node node) {
        return sortTracks(node.getTracks(), getConnectedNodeTracks(fromTracks, node));
    }

    private static <T extends Track> List<T> sortTracks(List<T> allTracks, Collection<? extends Track> selected) {
        return allTracks.stream().filter(selected::contains).collect(Collectors.toList());
    }
}
