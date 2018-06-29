package net.parostroj.timetable.model;

/**
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
