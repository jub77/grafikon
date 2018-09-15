package net.parostroj.timetable.model;

import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Line track.
 *
 * @author jub
 */
public class LineTrack extends Track implements Visitable {
    /**
     * Constructor.
     *
     * @param id id
     */
    public LineTrack(String id) {
        super(id);
    }

    /**
     * Constructor with number of the track.
     *
     * @param id id
     * @param number track number
     */
    public LineTrack(String id, String number) {
        super(id, number);
    }

    public NodeTrack getFromStraightTrack() {
        return this.getAttributes().get(ATTR_FROM_STRAIGHT, NodeTrack.class);
    }

    public void setFromStraightTrack(NodeTrack fromStraightTrack) {
        this.getAttributes().setRemove(ATTR_FROM_STRAIGHT, fromStraightTrack);
    }

    public NodeTrack getToStraightTrack() {
        return this.getAttributes().get(ATTR_TO_STRAIGHT, NodeTrack.class);
    }

    public void setToStraightTrack(NodeTrack toStraightTrack) {
        this.getAttributes().setRemove(ATTR_TO_STRAIGHT, toStraightTrack);
    }

    public NodeTrack getFromStraightTrack(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? getFromStraightTrack() : getToStraightTrack();
    }

    public NodeTrack getToStraightTrack(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? getToStraightTrack() : getFromStraightTrack();
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
}
