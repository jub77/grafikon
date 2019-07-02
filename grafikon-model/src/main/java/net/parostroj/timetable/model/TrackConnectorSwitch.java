package net.parostroj.timetable.model;

/**
 * Reprentation of switch between connector and node track.
 *
 * @author jub
 */
public interface TrackConnectorSwitch extends ObjectWithId, ItemCollectionObject, AttributesHolder {

    String ATTR_TRACK = "track";
    String ATTR_STRAIGHT = "straight";

    NodeTrack getNodeTrack();

    void setNodeTrack(NodeTrack nodeTrack);

    boolean isStraight();

    void setStraight(boolean straight);
}
