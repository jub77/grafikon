package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.Event;

/**
 * Implementation of track connector switch.
 *
 * @author jub
 */
public class TrackConnectorSwitchImpl implements TrackConnectorSwitch {

    private final String id;
    private final Attributes attributes;

    private boolean events = false;

    public TrackConnectorSwitchImpl(String id, Node node) {
        this.id = id;
        this.attributes = new Attributes((attrs, change) -> {
            if (events) {
                node.fireEvent(new Event(node, TrackConnectorSwitchImpl.this, change));
            }
        });
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void added() {
        this.events = true;
    }

    @Override
    public void removed() {
        this.events = false;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public NodeTrack getNodeTrack() {
        return this.attributes.get(ATTR_TRACK, NodeTrack.class);
    }

    @Override
    public void setNodeTrack(NodeTrack nodeTrack) {
        this.attributes.setRemove(ATTR_TRACK, nodeTrack);
    }

    @Override
    public boolean isStraight() {
        return this.attributes.getBool(ATTR_STRAIGHT);
    }

    @Override
    public void setStraight(boolean straight) {
        this.attributes.setBool(ATTR_STRAIGHT, straight);
    }
}
