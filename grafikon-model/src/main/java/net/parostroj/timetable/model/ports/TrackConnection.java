package net.parostroj.timetable.model.ports;

import java.util.Set;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.ObjectWithId;

/**
 * Track connection between line and node. Line track is connected
 * a certain connection point. The connection point is connected
 * to the set of node tracks (with possibility to specify straight
 * node track).
 *
 * @author jub
 */
public interface TrackConnection extends ObjectWithId {

    enum Orientation { LEFT, RIGHT }

    Orientation getOrientation();

    Node getNode();

    Line getLine();

    LineTrack getLineTrack();

    NodeTrack getStraightNodeTrack();

    Set<NodeTrack> getNodeTracks();
}
