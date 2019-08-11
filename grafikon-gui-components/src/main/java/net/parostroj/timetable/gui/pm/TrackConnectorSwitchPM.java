package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.*;
import org.beanfabrics.support.OnChange;

/**
 * Presentation model for track connector switch. If the connected value
 * is <code>true</code> then the node track is included in the connector.
 *
 * @author jub
 */
public class TrackConnectorSwitchPM extends AbstractPM {

    NodeTrackPM track;
    IBooleanPM connected;
    IBooleanPM straight;

    public TrackConnectorSwitchPM(NodeTrackPM track, boolean connected, boolean straight) {
        this.track = track;
        this.connected = new BooleanPM();
        this.connected.setBoolean(connected);
        this.straight = new BooleanPM();
        this.straight.setBoolean(straight);
        // update straight
        enableDisableStraight();
        PMManager.setup(this);
    }

    public NodeTrackPM getTrack() {
        return track;
    }

    public IBooleanPM getConnected() {
        return connected;
    }

    public IBooleanPM getStraight() {
        return straight;
    }

    /**
     * Disable straight property if the track is not connected.
     */
    @OnChange(path = { "connected" })
    public void enableDisableStraight() {
        boolean c = connected.getBoolean();
        if (!c) {
            straight.setBoolean(false);
        }
        straight.setEditable(c);
    }
}
