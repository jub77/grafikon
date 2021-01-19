package net.parostroj.timetable.model.computation;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TimeIntervalDirection;
import net.parostroj.timetable.model.Track;

class DefaultRouteTracksComputation implements RouteTracksComputation {

    static final DefaultRouteTracksComputation INSTANCE = new DefaultRouteTracksComputation();

    private final TrackConnectionComputation connectionComputation;

    private DefaultRouteTracksComputation() {
        connectionComputation = new TrackConnectionComputation();
    }

    @Override
    public Set<LineTrack> getAvailableLineTracks(Collection<? extends Track> fromTracks, Line line,
                                                 TimeIntervalDirection direction, Collection<? extends Track> toTracks) {
        Set<LineTrack> ltFrom = getConnectedLineTracks(fromTracks, line, direction);
        Set<LineTrack> ltTo = getConnectedLineTracks(toTracks, line, direction.reverse());
        ltTo.retainAll(ltFrom);
        return ltTo;
    }

    @Override
    public Set<NodeTrack> getAvailableNodeTracks(Collection<? extends Track> fromTracks, Node node,
                                                 Collection<? extends Track> toTracks) {
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

    private Set<LineTrack> getConnectedLineTracks(Collection<? extends Track> fromTracks, Line line,
            TimeIntervalDirection direction) {
        return connectionComputation.getConnectedLineTracks(fromTracks, line, direction);
    }

    private Set<NodeTrack> getConnectedNodeTracks(Collection<? extends Track> fromTracks,
            Node node) {
        return connectionComputation.getConnectedNodeTracks(fromTracks, node);
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
