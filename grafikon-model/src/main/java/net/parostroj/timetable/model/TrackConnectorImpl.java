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

    private final String id;
    private final NodePort nodePort;
    private final Attributes attributes;

    private boolean events = false;

    TrackConnectorImpl(String id, NodePort nodePort, String number) {
        this.id = id;
        this.nodePort = nodePort;
        this.attributes = new Attributes((attrs, change) -> {
            if (events) {
                Node node = nodePort.getNode();
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
    public NodePort getNodePort() {
        return nodePort;
    }

    @Override
    public Line.Endpoint getLineEndPoint() {
        return attributes.get(ATTR_LINE_ENDPOINT, Line.Endpoint.class);
    }

    @Override
    public void setLineEndpoint(Line.Endpoint lineEndpoint) {
        attributes.setRemove(ATTR_LINE_ENDPOINT, lineEndpoint);
    }

    @Override
    public NodeTrack getStraightNodeTrack() {
        return attributes.get(ATTR_STRAIGHT_TRACK, NodeTrack.class);
    }

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
