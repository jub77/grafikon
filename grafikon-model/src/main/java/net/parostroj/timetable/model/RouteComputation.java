package net.parostroj.timetable.model;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author jub
 */
public interface RouteComputation {

    /**
     * Returns available node tracks which connect from and to line tracks.
     * When no source tracks are specified - the first node, in case of no
     * destination tracks - the last node.
     *
     * @param fromTracks source tracks
     * @param node node
     * @param toTracks destination tracks
     * @return available node tracks
     */
    Set<NodeTrack> getAvailableNodeTracks(Collection<LineTrack> fromTracks, Node node,
            Collection<LineTrack> toTracks);

    /**
     * Returns available line tracks which connect from and to node tracks.
     *
     * @param fromTracks source tracks
     * @param line line
     * @param direction direction of movement
     * @param toTracks destination tracks
     * @return available line tracks
     */
    Set<LineTrack> getAvailableLineTracks(Collection<NodeTrack> fromTracks, Line line,
            TimeIntervalDirection direction, Collection<NodeTrack> toTracks);

    /**
     * Returns list of available node tracks. The line before and after has to
     * have already assigned tracks (first and last train interval is exception).
     *
     * @param nodeInterval node interval
     * @return list of available node tracks (previous interval has to have assigned
     *         track)
     */
    List<NodeTrack> getAvailableNodeTracks(TimeInterval nodeInterval);

    /**
     * Returns list of available line tracks. The node before and after has to
     * have already assigned tracks.
     *
     * @param lineInterval line interval
     * @return list of available line tracks (previous interval has to have assigned
     *         track)
     */
    List<LineTrack> getAvailableLineTracks(TimeInterval lineInterval);

    default List<? extends Track> getAvailableTracks(TimeInterval interval) {
        return interval.isNodeOwner() ? this.getAvailableNodeTracks(interval)
                : this.getAvailableLineTracks(interval);
    }

    static RouteComputation getDefaultInstance() {
        return new DefaultRouteComputation();
    }
}

class DefaultRouteComputation implements RouteComputation {

    @Override
    public Set<LineTrack> getAvailableLineTracks(Collection<NodeTrack> fromTracks, Line line,
            TimeIntervalDirection direction, Collection<NodeTrack> toTracks) {
        Set<LineTrack> ltFrom = getConnectedLineTracks(fromTracks, line, direction);
        Set<LineTrack> ltTo = getConnectedLineTracks(toTracks, line, direction.reverse());
        ltTo.retainAll(ltFrom);
        return ltTo;
    }

    @Override
    public Set<NodeTrack> getAvailableNodeTracks(Collection<LineTrack> fromTracks, Node node,
            Collection<LineTrack> toTracks) {
        if (toTracks.isEmpty()) {
            return getConnectedNodeTracks(fromTracks, node);
        } else if (fromTracks.isEmpty()) {
            return getConnectedNodeTracks(toTracks, node);
        } else {
            Set<NodeTrack> ntFrom = getConnectedNodeTracks(fromTracks, node);
            Set<NodeTrack> ntTo = getConnectedNodeTracks(toTracks, node);
            ntTo.retainAll(ntFrom);
            return ntTo;
        }
    }

    private Set<LineTrack> getConnectedLineTracks(Collection<NodeTrack> fromTracks, Line line,
            TimeIntervalDirection direction) {
        return line.getFrom(direction).getConnectors().stream()
                .filter(c -> c.getLineTrack().map(LineTrack::getOwner).orElse(null) == line)
                .filter(c -> c.getSwitches().stream()
                        .anyMatch(sw -> fromTracks.contains(sw.getNodeTrack())))
                .map(c -> c.getLineTrack().get())
                .collect(toSet());
    }

    private Set<NodeTrack> getConnectedNodeTracks(Collection<LineTrack> fromTracks, Node node) {
        return fromTracks.stream()
                .map(lt -> node.getConnectors().getForLineTrack(lt))
                .filter(Optional::isPresent)
                .flatMap(c -> c.get().getSwitches().stream())
                .map(TrackConnectorSwitch::getNodeTrack)
                .distinct()
                .collect(toSet());
    }

    @Override
    public List<NodeTrack> getAvailableNodeTracks(TimeInterval nodeInterval) {
        if (!nodeInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals supported");
        }

        Set<NodeTrack> tracks = getAvailableNodeTracks(
                nodeInterval.isFirst() ? Collections.emptyList()
                        : Collections.singletonList(
                                (LineTrack) nodeInterval.getPreviousTrainInterval().getTrack()),
                nodeInterval.getOwnerAsNode(),
                nodeInterval.isLast() ? Collections.emptyList()
                        : Collections.singletonList(
                                (LineTrack) nodeInterval.getNextTrainInterval().getTrack()));

        return nodeInterval.getOwnerAsNode().getTracks().stream().filter(tracks::contains)
                .collect(toList());
    }

    @Override
    public List<LineTrack> getAvailableLineTracks(TimeInterval lineInterval) {
        if (!lineInterval.isLineOwner()) {
            throw new IllegalArgumentException("Only line intervals supported");
        }

        Set<LineTrack> tracks = getAvailableLineTracks(
                Collections.singletonList(
                        (NodeTrack) lineInterval.getPreviousTrainInterval().getTrack()),
                lineInterval.getOwnerAsLine(), lineInterval.getDirection(), Collections
                        .singletonList((NodeTrack) lineInterval.getNextTrainInterval().getTrack()));

        return lineInterval.getOwnerAsLine().getTracks().stream().filter(tracks::contains)
                .collect(toList());
    }
}
