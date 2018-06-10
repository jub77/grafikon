package net.parostroj.timetable.model;

import java.util.Objects;
import java.util.Set;

import net.parostroj.timetable.model.events.Event;

/**
 * Implementation of track connector.
 *
 * @author jub
 */
public class TrackConnectorImpl implements TrackConnector {

    private final String id;
    private final Node node;
    private final Attributes attributes;

    private boolean events = false;

    TrackConnectorImpl(String id, Node node) {
        this.id = id;
        this.node = node;
        this.attributes = new Attributes((attrs, change) -> {
            if (events) {
                node.fireEvent(new Event(node, TrackConnectorImpl.this, change));
            }
        });
        this.attributes.set(ATTR_ORIENTATION, Orientation.LEFT);
    }

    @Override
    public String getId() {
        return id;
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
    public Orientation getOrientation() {
        return attributes.get(ATTR_ORIENTATION, Orientation.class);
    }

    @Override
    public void setOrientation(Orientation orientation) {
        Objects.requireNonNull(orientation, "Orientation cannot be null");
        attributes.set(ATTR_ORIENTATION, orientation);
    }

    @Override
    public String getName() {
        return attributes.get(ATTR_NAME, String.class);
    }

    @Override
    public void setName(String name) {
        attributes.setRemove(ATTR_NAME, name);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Line getLine() {
        return attributes.get(ATTR_LINE, Line.class);
    }

    @Override
    public LineTrack getLineTrack() {
        return attributes.get(ATTR_LINE_TRACK, LineTrack.class);
    }

    @Override
    public void setLineAndTrack(Line line, LineTrack lineTrack) {
        if (line == null && lineTrack != null || line != null && lineTrack == null) {
            throw new IllegalArgumentException("Both line and line track has to have value or not");
        }
        attributes.setRemove(ATTR_LINE, line);
        attributes.setRemove(ATTR_LINE_TRACK, lineTrack);
    }

    @Override
    public NodeTrack getStraightNodeTrack() {
        return attributes.get(ATTR_STRAIGHT_TRACK, NodeTrack.class);
    }

    @Override
    public Set<NodeTrack> getNodeTracks() {
        return attributes.getAsSet(ATTR_TRACKS, NodeTrack.class);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        String name = getName();
        return name != null ? name : "-";
    }
}
