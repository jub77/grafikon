package net.parostroj.timetable.model;

import java.util.Optional;

/**
 * Extended container for track connectors with utility methods.
 *
 * @author jub
 */
public interface TrackConnectorSwitches extends ItemSet<TrackConnectorSwitch> {
    default Optional<TrackConnectorSwitch> getForNodeTrack(NodeTrack nodeTrack) {
        return this.find(s -> s.getNodeTrack() == nodeTrack);
    }

    default boolean containsNodeTrack(NodeTrack nodeTrack) {
        return this.getForNodeTrack(nodeTrack).isPresent();
    }
}

class TrackConnectorSwitchesImpl extends ItemSetImpl<TrackConnectorSwitch>
        implements TrackConnectorSwitches {

    public TrackConnectorSwitchesImpl(ItemSetEventCallback<TrackConnectorSwitch> eventCallback) {
        super(eventCallback);
    }
}
