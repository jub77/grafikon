package net.parostroj.timetable.model;

import java.util.Optional;

import net.parostroj.timetable.visitors.Visitable;

/**
 * Track connector between line and node. Line track is connected
 * a certain connection point. The connection point is connected
 * to the set of node tracks (with possibility to specify straight
 * node track).
 *
 * @author jub
 */
public interface TrackConnector
        extends ObjectWithId, ItemCollectionObject, AttributesHolder, Visitable {

    String ATTR_LINE_TRACK = "line.track";
    String ATTR_SWITCHES = "switches";
    String ATTR_NUMBER = "number";
    String ATTR_ORIENTATION = "orientation";
    String ATTR_POSITION = "position";

    Node getNode();

    Node.Side getOrientation();

    void setOrientation(Node.Side orientation);

    int getPosition();

    void setPosition(int position);

    String getNumber();

    void setNumber(String number);

    Optional<LineTrack> getLineTrack();

    void setLineTrack(Optional<LineTrack> lineTrack);

    ItemSet<TrackConnectorSwitch> getSwitches();

    TrackConnectorSwitch createSwitch(String id);

    default Optional<NodeTrack> getStraightNodeTrack() {
        return getSwitches().find(TrackConnectorSwitch::isStraight)
                .map(TrackConnectorSwitch::getNodeTrack);
    }
}
