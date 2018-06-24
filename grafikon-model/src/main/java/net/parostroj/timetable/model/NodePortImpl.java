package net.parostroj.timetable.model;

import net.parostroj.timetable.model.Node.Side;

/**
 * @author jub
 */
class NodePortImpl implements NodePort {

    private final Node owner;
    private final Side side;
    private final ItemWithIdList<TrackConnector> connectors;
    private Location location;

    NodePortImpl(Node owner, Side side) {
        this.owner = owner;
        this.side = side;
        this.location = new Location(0, 0);
        this.connectors = new ItemWithIdListImpl<>(owner::fireCollectionEventListObject);
    }

    @Override
    public void added() {
    }

    @Override
    public void removed() {
    }

    @Override
    public Node getNode() {
        return owner;
    }

    @Override
    public Side getOrientation() {
        return side;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public ItemWithIdList<TrackConnector> getConnectors() {
        return connectors;
    }

    @Override
    public String toString() {
        return String.format("port: %s [%s]", getOrientation(), getLocation());
    }
}
