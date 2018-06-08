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
public interface TrackConnector extends ObjectWithId, ItemListObject, AttributesHolder {

    String ATTR_NAME = "name";
    String ATTR_ORIENTATION = "orientation";

    enum Orientation { LEFT, RIGHT }

    Orientation getOrientation();

    void setOrientation(Orientation orientation);

    String getName();

    void setName(String name);

    Node getNode();

    Line getLine();

    LineTrack getLineTrack();

    NodeTrack getStraightNodeTrack();

    Set<NodeTrack> getNodeTracks();

    TrackConnector getNextInMultiline();

    void setNextInMultiline(TrackConnector connector);
}
