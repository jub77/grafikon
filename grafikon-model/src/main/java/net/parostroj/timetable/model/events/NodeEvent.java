package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Node event.
 * 
 * @author jub
 */
public class NodeEvent extends RouteSegmentEvent<Node, NodeTrack> {

    public NodeEvent(Node node, GTEventType type) {
        super(node, type);
    }
    
    public NodeEvent(Node node, AttributeChange change) {
        super(node, change);
    }
    
    public NodeEvent(Node node, AttributeChange change, NodeTrack track) {
        super(node, change, track);
    }

    public NodeEvent(Node node, GTEventType type, NodeTrack track) {
        super(node, type, track);
    }

    public NodeEvent(Node node, GTEventType type, NodeTrack track, int fromIndex, int toIndex) {
        super(node, type, track, fromIndex, toIndex);
    }

    public NodeEvent(Node node, GTEventType type, TimeInterval interval) {
        super(node, type, interval);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("NodeEvent[");
        builder.append(getSource()).append(',');
        builder.append(getType());
        if (getType() == GTEventType.ATTRIBUTE || getType() == GTEventType.TRACK_ATTRIBUTE) {
            builder.append(',').append(getAttributeChange());
        }
        if (getTrack() != null) {
            builder.append(',').append(getTrack());
        }
        if (getInterval() != null) {
            builder.append(',').append(getInterval());
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
