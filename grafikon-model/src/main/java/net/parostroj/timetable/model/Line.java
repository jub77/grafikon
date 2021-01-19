package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.TrainDiagramTraversalVisitor;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Track between two route points.
 *
 * @author jub
 */
public class Line extends RouteSegmentImpl<LineTrack> implements RouteSegment<LineTrack>, AttributesHolder, ObjectWithId, Visitable,
        LineAttributes, TrainDiagramPart {

    /** Train diagram. */
    private final TrainDiagram diagram;
    /** Attributes. */
    private final Attributes attributes;

    /**
     * creates track with specified length.
     *
     * @param id id
     * @param diagram train diagram
     */
    Line(String id, TrainDiagram diagram) {
        super(id);
        this.attributes = new Attributes(
                (attrs, change) -> listenerSupport.fireEvent(new Event(Line.this, change)));
        this.diagram = diagram;
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
        return this.attributes.get(ATTR_LENGTH, Integer.class, 0);
    }

    /**
     * @param length length to be set
     */
    public void setLength(int length) {
        this.attributes.set(ATTR_LENGTH, length);
    }

    /**
     * @return top speed
     */
    public Integer getTopSpeed() {
        return this.attributes.get(ATTR_SPEED, Integer.class);
    }

    /**
     * @param topSpeed top speed to be set
     */
    public void setTopSpeed(Integer topSpeed) {
        if (topSpeed != null && topSpeed <= 0) {
            throw new IllegalArgumentException("Top speed should be positive number.");
        }
        this.attributes.setRemove(ATTR_SPEED, topSpeed);
    }

    @Override
    public String toString() {
        return this.toString(TimeIntervalDirection.FORWARD);
    }

    public String toString(TimeIntervalDirection direction) {
        return String.format("%s-%s", this.getFrom(direction).getAbbr(), this.getTo(direction).getAbbr());
    }

    public Node getFrom() {
        return diagram.getNet().getFrom(this);
    }

    public Node getTo() {
        return diagram.getNet().getTo(this);
    }

    public Node getFrom(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? getFrom() : getTo();
    }

    public Node getTo(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? getTo() : getFrom();
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
}
