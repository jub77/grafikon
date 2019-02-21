package net.parostroj.timetable.model;

import net.parostroj.timetable.model.Node.Side;

/**
 * Implementation of {@link NodePort}.
 *
 * @author jub
 */
class NodePortImpl implements NodePort {

    private final Node owner;
    private Side orientation;
    private final ItemWithIdList<TrackConnector> connectors;
    private int position;

    NodePortImpl(Node owner) {
        this.owner = owner;
        this.orientation = Side.LEFT;
        this.position = 0;
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
        return orientation;
    }

    @Override
    public void setOrientation(Side orientation) {
        this.orientation = orientation;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public ItemWithIdList<TrackConnector> getConnectors() {
        return connectors;
    }

    @Override
    public String toString() {
        return String.format("port: %s [%s]", getOrientation(), getPosition());
    }
}
