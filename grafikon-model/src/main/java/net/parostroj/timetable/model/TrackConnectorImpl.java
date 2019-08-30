package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;

/**
 * Implementation of track connector.
 *
 * @author jub
 */
public class TrackConnectorImpl implements TrackConnector {

    private final Node node;
    private final String id;
    private final Attributes attributes;

    private final ItemSet<TrackConnectorSwitch> switches;

    private boolean events = false;

    TrackConnectorImpl(String id, Node node) {
        this.id = id;
        this.node = node;
        this.attributes = new Attributes((attrs, change) -> {
            if (events) {
                node.fireEvent(new Event(TrackConnectorImpl.this, change));
            }
        });
        this.switches = new ItemSetImpl<>((type, item) -> {
            if (events) {
                node.fireEvent(new Event(TrackConnectorImpl.this, type, item));
            }
            // add/remove
            switch (type) {
                case ADDED:
                    item.added();
                    break;
                case REMOVED:
                    item.removed();
                    break;
                default: // nothing
                    break;
            }
        });
        this.setOrientation(Node.Side.LEFT);
        this.setPosition(0);
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
        return attributes.get(ATTR_ORIENTATION, Node.Side.class, Node.Side.LEFT);
    }

    @Override
    public void setOrientation(Node.Side orientation) {
        attributes.setRemove(ATTR_ORIENTATION, orientation);
    }

    @Override
    public int getPosition() {
        return attributes.get(ATTR_POSITION, Integer.class, 0);
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
    public ItemSet<TrackConnectorSwitch> getSwitches() {
        return switches;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public TrackConnectorSwitch createSwitch(String id) {
        return new TrackConnectorSwitchImpl(id, this);
    }

    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s[%s,%d]/%s",
                getNumber(),
                getOrientation(),
                getPosition(),
                getSwitches());
    }
}
