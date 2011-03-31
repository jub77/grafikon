package net.parostroj.timetable.model;

import java.util.HashSet;
import java.util.Set;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.TrainEvent;

/**
 * Time interval.
 *
 * @author jub
 */
public class TimeInterval implements AttributesHolder, ObjectWithId {

    public static final int DAY = 24 * 3600;

    private final String id;
    /** Interval. */
    private Interval interval;
    /** Train. */
    private Train train;
    /** Owner. */
    private RouteSegment owner;
    /** Track. */
    private Track track;
    /** Speed. */
    private int speed = NO_SPEED;
    /** Attributes. */
    private Attributes attributes;
    /** For tests - overlapping time intervals. */
    private Set<TimeInterval> overlappingIntervals;
    /** No speed constant. */
    public static final int NO_SPEED = -1;
    /** Direction of the time interval regarding the underlying line. */
    private TimeIntervalDirection direction;

    /**
     * creates instance of an time interval.
     *
     * @param id id
     * @param train train
     * @param owner owner (node track, node, ...)
     * @param start start time
     * @param end end time
     * @param speed speed for line time interval
     * @param direction direction of the line time interval
     * @param track track
     */
    public TimeInterval(String id, Train train, RouteSegment owner, int start, int end, int speed, TimeIntervalDirection direction, Track track) {
        this.train = train;
        this.setOwner(owner);
        this.interval = IntervalFactory.createInterval(start, end);
        this.speed = speed;
        this.direction = direction;
        this.track = track;
        this.attributes = new Attributes();
        this.id = id;
    }

    /**
     * creates instance of time interval.
     * 
     * @param id id
     * @param train train
     * @param owner time interval owner
     * @param start start time
     * @param end end time
     * @param track track
     */
    public TimeInterval(String id, Train train, RouteSegment owner, int start, int end, Track track) {
        this(id, train, owner, start, end, NO_SPEED, null, track);
    }

    /**
     * creates copy of an interval.
     * 
     * @param interval copied interval
     */
    public TimeInterval(String id, TimeInterval interval) {
        this(id, interval.getTrain(), interval.getOwner(), interval.getStart(),
                interval.getEnd(), interval.getSpeed(), interval.getDirection(),
                interval.getTrack());
        this.setAttributes(new Attributes(interval.getAttributes()));
    }

    /**
     * @return end time
     */
    public int getEnd() {
        return interval.getEnd();
    }

    /**
     * @param end end time to be set
     */
    public void setEnd(int end) {
        this.interval = IntervalFactory.createInterval(interval.getStart(), end);
    }

    /**
     * @return start time
     */
    public int getStart() {
        return interval.getStart();
    }

    /**
     * @param start start time to be set
     */
    public void setStart(int start) {
        this.interval = IntervalFactory.createInterval(start, interval.getEnd());
    }

    /**
     * @return interval
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * @return train
     */
    public Train getTrain() {
        return train;
    }

    /**
     * @param train train to be set
     */
    public void setTrain(Train train) {
        this.train = train;
    }

    /**
     * compares intervals for route part. Open/unbounded interval. It uses
     * normalized intervals.
     *
     * @param o interval
     * @return comparison
     */
    public int compareOpenNormalized(TimeInterval o) {
        return this.getInterval().compareOpenNormalized(o.getInterval());
    }

    /**
     * compares intervals for trains. Closed/bounded interval. It uses
     * normalized intervals.
     *
     * @param o interval
     * @return comparison
     */
    public int compareClosedNormalized(TimeInterval o) {
        return this.getInterval().compareClosedNormalized(o.getInterval());
    }

    @Override
    public String toString() {
        return train + "(" + getStart() + "," + getEnd() + ")";
    }

    /**
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * @return the track
     */
    public Track getTrack() {
        return track;
    }

    /**
     * @param track the track to set
     */
    public void setTrack(Track track) {
        this.track = track;
    }

    /**
     * @return the overlapping intervals
     */
    public Set<TimeInterval> getOverlappingIntervals() {
        if (overlappingIntervals == null) {
            overlappingIntervals = new HashSet<TimeInterval>();
        }
        return overlappingIntervals;
    }

    /**
     * @return <code>true</code> if there is overlapping interval
     */
    public boolean isOverlapping() {
        return (overlappingIntervals != null) && (overlappingIntervals.size() > 0);
    }

    /**
     * @param overlappingIntervals the collidingIntervals to set
     */
    public void setOverlappingIntervals(Set<TimeInterval> overlappingIntervals) {
        this.overlappingIntervals = overlappingIntervals;
    }

    /**
     * @return <code>false</code> if there is no platform for train that needs one (in any other case it returns <code>true</code>)
     */
    public boolean isPlatformOk() {
        if (this.isStop()) {
            return !(train.getType().isPlatform() && !((NodeTrack) track).isPlatform());
        } else {
            // otherwise ok
            return true;
        }
    }

    /**
     * shifts time interval with specified amount of time.
     * 
     * @param timeShift shift time
     */
    public void shift(int timeShift) {
        this.interval = IntervalFactory.createInterval(interval.getStart() + timeShift, interval.getEnd() + timeShift);
    }

    /**
     * moves interval to specified starting time.
     */
    public void move(int aStart) {
        int length = this.getLength();
        this.interval = IntervalFactory.createInterval(aStart, aStart + length);
    }

    /**
     * returns length of the interval.
     * 
     * @return length of the interval
     */
    public int getLength() {
        return interval.getEnd() - interval.getStart();
    }

    /**
     * sets length of the interval.
     * 
     * @param length new length of the interval
     */
    public void setLength(int length) {
        this.interval = IntervalFactory.createInterval(interval.getStart(), interval.getStart() + length);
    }

    /**
     * @return owner of this time interval
     */
    public RouteSegment getOwner() {
        return owner;
    }

    /**
     * @param owner new owner to be set
     */
    public void setOwner(RouteSegment owner) {
        this.owner = owner;
    }

    /**
     * @return direction
     */
    public TimeIntervalDirection getDirection() {
        return direction;
    }

    /**
     * @param direction new direction to be set
     */
    public void setDirection(TimeIntervalDirection direction) {
        this.direction = direction;
    }

    /**
     * @return from node for interval that belongs to line otherwise <code>null</code>
     */
    public Node getFrom() {
        return (owner instanceof Line) ? ((Line) owner).getFrom(direction) : null;
    }

    /**
     * @return to node for interval that belongs to line otherwise <code>null</code>
     */
    public Node getTo() {
        return (owner instanceof Line) ? ((Line) owner).getTo(direction) : null;
    }

    /**
     * @return from node track straight for interval that belongs to line otherwise <code>null</code>
     */
    public NodeTrack getFromStraightTrack() {
        return (owner instanceof Line) ? ((LineTrack) track).getFromStraightTrack(direction) : null;
    }

    /**
     * @return to node straight track for interval that belongs to line otherwise <code>null</code>
     */
    public NodeTrack getToStraightTrack() {
        return (owner instanceof Line) ? ((LineTrack) track).getToStraightTrack(direction) : null;
    }
    
    /**
     * @return line class for interval that belongs to line (otherwise an error is thrown).
     */
    public LineClass getLineClass() {
        if (isLineOwner()) {
            return getOwnerAsLine().getLineClass(direction);
        } else
            throw new IllegalStateException("Cannot get line class for node interval.");
    }

    public void removeFromOwner() {
        if (!isAttached())
            throw new IllegalStateException("Time interval is not attached.");
        owner.removeTimeInterval(this);
    }

    public void addToOwner() {
        if (isAttached())
            throw new IllegalStateException("Time interval is already attached.");
        owner.addTimeInterval(this);
    }

    public void addToOwnerWithoutCheck() {
        owner.addTimeInterval(this);
    }

    public void updateInOwner() {
        if (!isAttached())
            throw new IllegalStateException("Time interval is not attached.");
        owner.updateTimeInterval(this);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        Object oldValue = attributes.get(key);
        attributes.put(key, value);
        train.fireEvent(new TrainEvent(train,
                new AttributeChange(key, oldValue, value),
                train.getTimeIntervalList().indexOf(this)));
    }

    @Override
    public Object removeAttribute(String key) {
        Object oldValue = attributes.remove(key);
        if (oldValue != null)
            train.fireEvent(new TrainEvent(train,
                    new AttributeChange(key, oldValue, null),
                    train.getTimeIntervalList().indexOf(this)));
        return oldValue;
    }
    
    public boolean isNodeOwner() {
        return (owner instanceof Node);
    }
    
    public boolean isLineOwner() {
        return (owner instanceof Line);
    }
    
    public Node getOwnerAsNode() {
        return isNodeOwner() ? (Node)owner : null;
    }
    
    public Line getOwnerAsLine() {
        return isLineOwner() ? (Line)owner : null;
    }

    public boolean isTechnological() {
        return train.getTimeIntervalBefore() == this || train.getTimeIntervalAfter() == this;
    }

    public boolean isStop() {
        return isInnerStop() || isFirst() || isLast();
    }

    public boolean isInnerStop() {
        return isNodeOwner() && getLength() != 0;
    }

    public boolean isFirst() {
        return train.getFirstInterval() == this;
    }

    public boolean isLast() {
        return train.getLastInterval() == this;
    }

    public boolean isBoundary() {
        return isFirst() || isLast();
    }

    public TimeInterval getNextTrainInterval() {
        return train.getIntervalAfter(this);
    }

    public TimeInterval getPreviousTrainInterval() {
        return train.getIntervalBefore(this);
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isAttached() {
        return train.isAttached();
    }
}
