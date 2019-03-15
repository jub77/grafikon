package net.parostroj.timetable.model;

import java.util.Set;

/**
 * Track connector between line and node. Line track is connected
 * a certain connection point. The connection point is connected
 * to the set of node tracks (with possibility to specify straight
 * node track).
 *
 * @author jub
 */
public interface TrackConnector extends ObjectWithId, ItemCollectionObject, AttributesHolder {

    String ATTR_LINE_TRACK = "line.track";
    String ATTR_STRAIGHT_TRACK = "straight.track";
    String ATTR_TRACKS = "tracks";
    String ATTR_NUMBER = "number";

    NodePort getNodePort();

    String getNumber();

    void setNumber(String number);

    LineTrack getLineTrack();

    void setLineTrack(LineTrack lineTrack);

    NodeTrack getStraightNodeTrack();

    void setStraightNodeTrack(NodeTrack nodeTrack);

    Set<NodeTrack> getNodeTracks();
}
