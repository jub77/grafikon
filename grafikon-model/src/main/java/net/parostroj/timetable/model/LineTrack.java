package net.parostroj.timetable.model;

import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.visitors.TrainDiagramVisitor;
import net.parostroj.timetable.visitors.Visitable;

/**
 * Line track.
 *
 * @author jub
 */
public class LineTrack extends Track implements Visitable {

    private NodeTrack fromStraightTrack;
    private NodeTrack toStraightTrack;

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
        return fromStraightTrack;
    }

    public void setFromStraightTrack(NodeTrack fromStraightTrack) {
        NodeTrack oldTrack = this.fromStraightTrack;
        if (!ObjectsUtil.compareWithNull(oldTrack, fromStraightTrack)) {
            this.fromStraightTrack = fromStraightTrack;
            this.fireAttributeChanged(ATTR_FROM_STRAIGHT, oldTrack, fromStraightTrack);
        }
    }

    public NodeTrack getToStraightTrack() {
        return toStraightTrack;
    }

    public void setToStraightTrack(NodeTrack toStraightTrack) {
        NodeTrack oldTrack = this.toStraightTrack;
        if (!ObjectsUtil.compareWithNull(oldTrack, toStraightTrack)) {
            this.toStraightTrack = toStraightTrack;
            this.fireAttributeChanged(ATTR_TO_STRAIGHT, oldTrack, toStraightTrack);
        }
    }

    public NodeTrack getFromStraightTrack(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? fromStraightTrack : toStraightTrack;
    }

    public NodeTrack getToStraightTrack(TimeIntervalDirection direction) {
        return (direction == TimeIntervalDirection.FORWARD) ? toStraightTrack : fromStraightTrack;
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
