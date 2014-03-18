package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Line event.
 * 
 * @author jub
 */
public class LineEvent extends RouteSegmentEvent<Line, LineTrack> {

    public LineEvent(Line line, GTEventType type) {
        super(line, type);
    }
    
    public LineEvent(Line line, GTEventType type, LineTrack track) {
        super(line, type, track);
    }

    public LineEvent(Line line, GTEventType type, LineTrack track, int fromIndex, int toIndex) {
        super(line, type, track, fromIndex, toIndex);
    }

    public LineEvent(Line line, AttributeChange attributeChange) {
        super(line, attributeChange);
    }
    
    public LineEvent(Line line, AttributeChange attributeChange, LineTrack track) {
        super(line, attributeChange, track);
    }

    public LineEvent(Line line, GTEventType type, TimeInterval interval) {
        super(line, type, interval);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("LineEvent[");
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
