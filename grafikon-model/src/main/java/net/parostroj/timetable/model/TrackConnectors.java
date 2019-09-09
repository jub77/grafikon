package net.parostroj.timetable.model;

import java.util.Optional;
import java.util.Set;

/**
 * Extended container for track connectors with utility methods.
 *
 * @author jub
 */
public interface TrackConnectors extends ItemWithIdSet<TrackConnector> {

    default public Optional<TrackConnector> getForLineTrack(LineTrack lineTrack) {
        return this.find(conn -> conn.getLineTrack().orElse(null) == lineTrack);
    }

    default public Set<TrackConnector> getWithoutLineTrack() {
        return this.findAll(conn -> !conn.getLineTrack().isPresent());
    }

    default public Set<TrackConnector> getForSideAndNodeTrack(Node.Side side, NodeTrack nodeTrack) {
        return this.findAll(conn -> conn.getOrientation() == side
                && conn.getSwitches().find(s -> s.getNodeTrack() == nodeTrack).isPresent());
    }
}

class TrackConnectorsImpl extends ItemWithIdSetImpl<TrackConnector> implements TrackConnectors {

    public TrackConnectorsImpl(ItemSetEventCallback<TrackConnector> eventCallback) {
        super(eventCallback);
    }
}
