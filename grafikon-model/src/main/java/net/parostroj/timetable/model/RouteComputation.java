package net.parostroj.timetable.model;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

/**
 * @author jub
 */
public interface RouteComputation {

    /**
     * @param nodeInterval node interval
     * @return list of available node tracks (previous interval has to have assigned
     *         track)
     */
    List<NodeTrack> getAvailableNodeTracks(TimeInterval nodeInterval);

    /**
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
    public List<NodeTrack> getAvailableNodeTracks(TimeInterval nodeInterval) {
        if (!nodeInterval.isNodeOwner()) {
            throw new IllegalArgumentException("Only node intervals supported");
        }
        if (nodeInterval.isFirst()) {
            return nodeInterval.getOwnerAsNode().getTracks();
        } else {
            Optional<TrackConnector> connector = nodeInterval.getFromTrackConnector();
            if (!connector.isPresent()) {
                throw new IllegalStateException("No connector available");
            }
            return nodeInterval.getOwnerAsNode().getTracks().stream()
                    .filter(t -> connector.get().getSwitches().containsNodeTrack(t))
                    .collect(toList());
        }
    }

    @Override
    public List<LineTrack> getAvailableLineTracks(TimeInterval lineInterval) {
        if (!lineInterval.isLineOwner()) {
            throw new IllegalArgumentException("Only line intervals supported");
        }
        NodeTrack nodeTrack = (NodeTrack) lineInterval.getPreviousTrainInterval().getTrack();
        Node node = nodeTrack.getOwner();
        return lineInterval.getOwnerAsLine().getTracks().stream()
                .filter(l -> node.getConnectors().getForLineTrack(l)
                        .map(c -> c.getSwitches().containsNodeTrack(nodeTrack)).orElse(false))
                .collect(toList());
    }
}
