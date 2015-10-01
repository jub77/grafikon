package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Track between two route points.
 *
 * @author jub
 */
public class Line extends RouteSegmentImpl<LineTrack> implements RouteSegment, AttributesHolder, ObjectWithId, Visitable, LineAttributes, TrainDiagramPart {

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** Length in mm. */
    private int length;
    /** Top speed for the track. */
    private Integer topSpeed;
    /** Attributes. */
    private final Attributes attributes;
    /** Starting point. */
    private final Node from;
    /** Ending point. */
    private final Node to;
    private final GTListenerSupport<LineListener, LineEvent> listenerSupport;

    /**
     * creates track with specified length.
     *
     * @param id id
     * @param diagram train diagram
     * @param length length of the track in milimeters
     * @param from starting point
     * @param to end point
     * @param topSpeed top speed
     */
    Line(String id, TrainDiagram diagram, int length, Node from, Node to, Integer topSpeed) {
        super(id);
        this.listenerSupport = new GTListenerSupport<LineListener, LineEvent>(
                (listener, event) -> listener.lineChanged(event));
        this.attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new LineEvent(Line.this, change)));
        this.length = length;
        this.from = from;
        this.to = to;
        this.diagram = diagram;
        this.topSpeed = topSpeed;
    }

    @Override
	public Attributes getAttributes() {
        return attributes;
    }


    @Override
    public TrainDiagram getDiagram() {
        return diagram;
    }

    /**
     * @return track length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length length to be set
     */
    public void setLength(int length) {
        if (length != this.length) {
            int oldLength = this.length;
            this.length = length;
            this.listenerSupport.fireEvent(new LineEvent(this, new AttributeChange(ATTR_LENGTH, oldLength, length)));
        }
    }

    public LineTrack selectTrack(TimeInterval interval, LineTrack preselectedTrack) {
        LineTrack selectedTrack = preselectedTrack;
        if (selectedTrack == null || selectedTrack.testTimeInterval(interval).getStatus() != TimeIntervalResult.Status.OK) {
            // check which track is free for adding
            for (LineTrack lineTrack : this.getIterableByDirection(interval.getDirection())) {
                TimeIntervalResult result = lineTrack.testTimeInterval(interval);
                if (result.getStatus() == TimeIntervalResult.Status.OK) {
                    selectedTrack = lineTrack;
                    break;
                }
            }
        }
        if (selectedTrack == null) {
            // set first one
            selectedTrack = tracks.get(interval.getDirection() == TimeIntervalDirection.FORWARD ? 0 : tracks.size() - 1);
        }
        return selectedTrack;
    }

    /**
     * @return top speed
     */
    public Integer getTopSpeed() {
        return topSpeed;
    }

    /**
     * @param topSpeed top speed to be set
     */
    public void setTopSpeed(Integer topSpeed) {
        if (topSpeed != null && topSpeed <= 0) {
            throw new IllegalArgumentException("Top speed should be positive number.");
        }
        if (!ObjectsUtil.compareWithNull(topSpeed, this.topSpeed)) {
            Integer oldTopSpeed = this.topSpeed;
            this.topSpeed = topSpeed;
            this.listenerSupport.fireEvent(new LineEvent(this, new AttributeChange(ATTR_SPEED, oldTopSpeed, topSpeed)));
        }
    }

    @Override
    public boolean isLine() {
        return true;
    }

    @Override
    public boolean isNode() {
        return false;
    }

    @Override
    public String toString() {
        return this.toString(TimeIntervalDirection.FORWARD);
    }

    public String toString(TimeIntervalDirection direction) {
        return String.format("%s-%s", this.getFrom(direction).getAbbr(), this.getTo(direction).getAbbr());
    }

    @Override
    public Line asLine() {
        return this;
    }

    @Override
    public Node asNode() {
        return null;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public Node getFrom(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? from : to;
    }

    public Node getTo(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? to : from;
    }

    public LineClass getLineClass(TimeIntervalDirection direction) {
        LineClass result = null;
        if (direction == TimeIntervalDirection.BACKWARD) {
            result = getAttribute(ATTR_CLASS_BACK, LineClass.class);
        }
        if (direction == TimeIntervalDirection.FORWARD || result == null) {
            result = getAttribute(ATTR_CLASS, LineClass.class);
        }
        return result;
    }

    @Override
    public <T> T getAttribute(String key, Class<T> clazz) {
        return attributes.get(key, clazz);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.set(key, value);
    }

    @Override
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public void addListener(LineListener listener) {
        this.listenerSupport.addListener(listener);
    }

    public void removeListener(LineListener listener) {
        this.listenerSupport.removeListener(listener);
    }

    @Override
    protected void fireTimeIntervalEvent(TimeInterval interval, GTEventType eventType) {
        this.listenerSupport.fireEvent(new LineEvent(this, eventType, interval));
    }

    @Override
    protected void fireTrackAttributeChanged(String attributeName, Track track, Object oldValue, Object newValue) {
        this.listenerSupport.fireEvent(new LineEvent(this, new AttributeChange(attributeName, oldValue, newValue), (LineTrack) track));
    }

    @Override
    protected void fireTrackEvent(Track track, GTEventType eventType, Integer from, Integer to) {
        if (from == null && to == null) {
            this.listenerSupport.fireEvent(new LineEvent(this, eventType, (LineTrack) track));
        } else {
            this.listenerSupport.fireEvent(new LineEvent(this, eventType, (LineTrack) track, from, to));
        }
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    @Override
    public void accept(TrainDiagramVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * accepts visitor.
     *
     * @param visitor visitor
     */
    public void accept(TrainDiagramTraversalVisitor visitor) {
        visitor.visit(this);
        for (LineTrack track : tracks) {
            track.accept(visitor);
        }
        visitor.visitAfter(this);
    }

    private Iterable<LineTrack> getIterableByDirection(TimeIntervalDirection direction) {
        if (direction == TimeIntervalDirection.FORWARD) {
            return tracks;
        } else {
            return new Iterable<LineTrack>() {
                @Override
                public Iterator<LineTrack> iterator() {
                    return new Iterator<LineTrack>() {
                        private final ListIterator<LineTrack> i = tracks.listIterator(tracks.size());

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public LineTrack next() {
                            return i.previous();
                        }

                        @Override
                        public boolean hasNext() {
                            return i.hasPrevious();
                        }
                    };
                }
            };
        }
    }
}
