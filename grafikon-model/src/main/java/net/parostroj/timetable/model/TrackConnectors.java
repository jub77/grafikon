package net.parostroj.timetable.model;

import java.util.Optional;
import java.util.Set;

/**
 * Extended container for track connectors with utility methods.
 *
 * @author jub
 */
public interface TrackConnectors extends ItemWithIdSet<TrackConnector> {

    default Optional<TrackConnector> getForLineTrack(LineTrack lineTrack) {
        return this.find(conn -> conn.getLineTrack().orElse(null) == lineTrack);
    }

    default Set<TrackConnector> getWithoutLineTrack() {
        return this.findAll(conn -> conn.getLineTrack().isEmpty());
    }

    default Set<TrackConnector> getForSideAndNodeTrack(Node.Side side, NodeTrack nodeTrack) {
        return this.findAll(conn -> conn.getOrientation() == side
                && conn.getSwitches().find(s -> s.getNodeTrack() == nodeTrack).isPresent());
    }

    default Set<TrackConnector> getForLine(Line line) {
        return this.findAll(c -> c.getLineTrack().map(LineTrack::getOwner).orElse(null) == line);
    }
}

class TrackConnectorsImpl extends ItemWithIdSetImpl<TrackConnector> implements TrackConnectors {

    public TrackConnectorsImpl(ItemSetEventCallback<TrackConnector> eventCallback) {
        super(eventCallback);
    }
}
