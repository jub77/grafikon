package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.Set;

import net.parostroj.timetable.model.events.Event;

/**
 * Implementation of track connector.
 *
 * @author jub
 */
public class TrackConnectorImpl implements TrackConnector {

    private final Node node;
    private final String id;
    private final Attributes attributes;

    private boolean events = false;

    TrackConnectorImpl(String id, Node node, String number) {
        this.id = id;
        this.node = node;
        this.attributes = new Attributes((attrs, change) -> {
            if (events) {
                node.fireEvent(new Event(node, TrackConnectorImpl.this, change));
            }
        });
        this.setNumber(number);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Node.Side getOrientation() {
        return attributes.get(ATTR_ORIENTATION, Node.Side.class);
    }

    @Override
    public void setOrientation(Node.Side orientation) {
        attributes.setRemove(ATTR_ORIENTATION, orientation);
    }

    @Override
    public int getPosition() {
        return attributes.get(ATTR_POSITION, Integer.class);
    }

    @Override
    public void setPosition(int position) {
        attributes.set(ATTR_POSITION, position);
    }

    @Override
    public String getNumber() {
        return attributes.get(ATTR_NUMBER, String.class);
    }

    @Override
    public void setNumber(String number) {
        attributes.setRemove(ATTR_NUMBER, number);
    }

    @Override
    public void added() {
        events = true;
    }

    @Override
    public void removed() {
        events = false;
    }

    @Override
    public LineTrack getLineTrack() {
        return attributes.get(ATTR_LINE_TRACK, LineTrack.class);
    }

    @Override
    public void setLineTrack(LineTrack lineTrack) {
        attributes.setRemove(ATTR_LINE_TRACK, lineTrack);
    }

    @Override
    public NodeTrack getStraightNodeTrack() {
        return attributes.get(ATTR_STRAIGHT_TRACK, NodeTrack.class);
    }

    @Override
    public void setStraightNodeTrack(NodeTrack nodeTrack) { attributes.setRemove(ATTR_STRAIGHT_TRACK, nodeTrack); }

    @Override
    public Set<NodeTrack> getNodeTracks() {
        return attributes.getAsSet(ATTR_TRACKS, NodeTrack.class, Collections.emptySet());
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        NodeTrack straight = getStraightNodeTrack();
        return String.format("%s,%s", straight != null ? straight.toString() : "-", getNodeTracks());
    }
}
