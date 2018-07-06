package net.parostroj.timetable.model;

/**
 * Definition of line end to the station. It can has more than one
 * track. A line can use one or more tracks from the port. The limitation
 * is that one line can be only connected to one port. More than one
 * line can be connected to the port, if there is enough free
 * tracks ({@link TrackConnector}).
 *
 * @author jub
 */
public interface NodePort extends ItemCollectionObject {

    Node getNode();

    Node.Side getOrientation();

    void setOrientation(Node.Side orientation);

    int getPosition();

    void setPosition(int position);

    ItemWithIdList<TrackConnector> getConnectors();
}
