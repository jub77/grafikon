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
public interface TrackConnector extends ObjectWithId, ItemListObject {

    enum Orientation { LEFT, RIGHT }

    Orientation getOrientation();

    Node getNode();

    Line getLine();

    LineTrack getLineTrack();

    NodeTrack getStraightNodeTrack();

    Set<NodeTrack> getNodeTracks();
}
