package net.parostroj.timetable.model;

/**
 * @author jub
 */
public interface NodePort extends ItemCollectionObject {

    Node getNode();

    Node.Side getOrientation();

    Location getLocation();

    void setLocation(Location location);

    ItemWithIdList<TrackConnector> getConnectors();
}
