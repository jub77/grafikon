package net.parostroj.timetable.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.parostroj.timetable.model.events.Event;

/**
 * Time interval.
 *
 * @author jub
 */
public class TimeInterval implements AttributesHolder, ObjectWithId {

    public static final String ATTR_SET_SPEED = "set.speed";
    public static final String ATTR_IGNORE_LENGTH = "ignore.length";
    public static final String ATTR_COMMENT = "comment";
    public static final String ATTR_COMMENT_SHOWN = "comment.shown";
    public static final String ATTR_SHUNT = "shunt";
    public static final String ATTR_OCCUPIED = "occupied";
    public static final String ATTR_NOT_MANAGED_FREIGHT = "not.managed.freight";
    public static final String ATTR_NO_REGION_CENTER_TRANSFER = "no.region.center.transfer";

    public static final int DAY = 24 * 3600;
    public static final int HOUR = 3600;
    public static final int MINUTE = 60;
    public static final int SECOND = 1;

    private final String id;
    /** Interval. */
    private Interval interval;
    /** Train. */
    private Train train;
    /** Owner. */
    private NetSegment<? extends Track> owner;
    /** Track. */
    private Track track;
    /** Speed. */
    private Integer speedLimit;
    /** Added time. */
    private int addedTime;
    /** Attributes. */
    private final Attributes attributes;
    /** For tests - overlapping time intervals. */
    private Set<TimeInterval> overlappingIntervals;
    /** Direction of the time interval regarding the underlying line. */
    private TimeIntervalDirection direction;
    /** Speed used for calculation of running time (line as an owner). */
    private Integer usedSpeed;
    /** Changed - used for indication that the time interval for whatever reason changed. */
    private boolean changed;

    private TimeIntervalCalculation calculation;

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
     * @param addedTime added time
     */
    public TimeInterval(String id, Train train, NetSegment<? extends Track> owner, int start, int end, Integer speed, TimeIntervalDirection direction, Track track, int addedTime) {
        this.setTrain(train);
        this.setOwner(owner);
        this.interval = IntervalFactory.createInterval(start, end);
        this.speedLimit = speed;
        this.direction = direction;
        this.track = track;
        this.attributes = new Attributes((attrs, change) -> train.fireEvent(new Event(train, this, change)));
        this.id = id;
        this.addedTime = addedTime;
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
    public TimeInterval(String id, Train train, NetSegment<? extends Track> owner, int start, int end, Track track) {
        this(id, train, owner, start, end, null, null, track, 0);
    }

    /**
     * creates copy of an interval.
     *
     * @param id id of an interval
     * @param interval copied interval
     */
    public TimeInterval(String id, TimeInterval interval, Train train) {
        this(id, train, interval.getOwner(), interval.getStart(),
                interval.getEnd(), interval.getSpeedLimit(), interval.getDirection(),
                interval.getTrack(), interval.getAddedTime());
        this.getAttributes().add(interval.getAttributes());
    }

    private void setChanged() {
        this.changed = true;
    }

    private void clearChanged() {
        this.changed = false;
    }

    protected boolean isChanged() {
        return changed;
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
        if (end != interval.getEnd()) {
            this.setIntervalImpl(interval.getStart(), end);
            this.setChanged();
        }
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
        if (start != interval.getStart()) {
            this.setIntervalImpl(start, interval.getEnd());
            this.setChanged();
        }
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
        this.calculation = new TimeIntervalCalculation(train, this);
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
        TimeConverter converter = this.getTrain().getDiagram().getTimeConverter();
        String ownerStr = getOwner() != null ?
                (isNodeOwner() ? getOwner().toString() : getOwnerAsLine().toString(getDirection()))
                : "-";
        return getStart() != getEnd()
                ? String.format("%s[%s]{%s}(%s,%s)", ownerStr, getTrain().getName().translate(), getTrackString(),
                        converter.convertIntToText(getStart()), converter.convertIntToText(getEnd()))
                : String.format("%s[%s]{%s}(%s)", ownerStr, getTrain().getName().translate(), getTrackString(),
                        converter.convertIntToText(getStart()));
    }

    private String getTrackString() {
        return getTrack() == null ? "-" : getTrack().getNumber();
    }

    /**
     * @return the speed
     */
    public Integer getSpeedLimit() {
        return speedLimit;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeedLimit(Integer speed) {
        if (this.speedLimit != speed) {
            this.speedLimit = speed;
            this.setChanged();
        }
    }

    public Integer getSpeed() {
        return usedSpeed;
    }

    public void setSpeed(Integer usedSpeed) {
        if (this.usedSpeed != usedSpeed) {
            this.usedSpeed = usedSpeed;
            this.setChanged();
        }
    }

    /**
     * @return the addedTime
     */
    public int getAddedTime() {
        return addedTime;
    }

    /**
     * @param addedTime the addedTime to set
     */
    public void setAddedTime(int addedTime) {
        if (this.addedTime != addedTime) {
            this.addedTime = addedTime;
            this.setChanged();
        }
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
        if (track != this.track) {
            this.track = track;
            setChanged();
        }
    }

    /**
     * @return the overlapping intervals
     */
    public Set<TimeInterval> getOverlappingIntervals() {
        if (overlappingIntervals == null) {
            overlappingIntervals = new HashSet<>();
        }
        return overlappingIntervals;
    }

    /**
     * @return <code>true</code> if there is overlapping interval
     */
    public boolean isOverlapping() {
        return (overlappingIntervals != null) && !overlappingIntervals.isEmpty();
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
        if (timeShift != 0) {
            this.setIntervalImpl(interval.getStart() + timeShift, interval.getEnd() + timeShift);
            this.setChanged();
        }
    }

    /**
     * moves interval to specified starting time.
     */
    public void move(int aStart) {
        if (aStart != this.interval.getStart()) {
            int length = this.getLength();
            this.setIntervalImpl(aStart, aStart + length);
            this.setChanged();
        }
    }

    /**
     * returns length of the interval.
     *
     * @return length of the interval
     */
    public int getLength() {
        return interval.getLength();
    }

    /**
     * sets length of the interval.
     *
     * @param length new length of the interval
     */
    public void setLength(int length) {
        if (length != this.getLength()) {
            this.setIntervalImpl(interval.getStart(), interval.getStart() + length);
            this.setChanged();
        }
    }

    /**
     * sets interval.
     *
     * @param start start
     * @param end end
     */
    public void setInterval(int start, int end) {
        if (start != interval.getStart() || end != interval.getEnd()) {
            this.setIntervalImpl(start, end);
            this.setChanged();
        }
    }

    private void setIntervalImpl(int start, int end) {
        this.interval = IntervalFactory.createInterval(start, end);
        if (this.getLength() == 0) {
            this.removeAttribute(TimeInterval.ATTR_NOT_MANAGED_FREIGHT);
        }
    }

    /**
     * @return owner of this time interval
     */
    public NetSegment<? extends Track> getOwner() {
        return owner;
    }

    /**
     * @param owner new owner to be set
     */
    public void setOwner(NetSegment<? extends Track> owner) {
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
     * @return if the current interval is straight from previous one
     */
    public boolean isFromStraight() {
        if (isNodeOwner()) {
            return this.getFromTrackConnector()
                    .map(c -> c.getStraightNodeTrack().orElse(null) == getTrack()).orElse(false);
        } else {
            return this.getPreviousTrainInterval().isToStraight();
        }
    }

    /**
     * @return if the current interval is straight to next one
     */
    public boolean isToStraight() {
        if (isNodeOwner()) {
            return this.getToTrackConnector()
                    .map(c -> c.getStraightNodeTrack().orElse(null) == getTrack()).orElse(false);
        } else {
            return this.getNextTrainInterval().isFromStraight();
        }
    }

    /**
     * @return connector used for arrival to the route segment
     */
    public Optional<TrackConnector> getFromTrackConnector() {
        TimeInterval previousInterval = this.getPreviousTrainInterval();
        if (isNodeOwner()) {
            Node node = getOwnerAsNode();
            return previousInterval == null ? Optional.empty() :
                node.getConnectors().getForLineTrack((LineTrack) previousInterval.getTrack());
        } else {
            return previousInterval.getToTrackConnector();
        }
    }

    /**
     * @return connector used for departure from the route segment
     */
    public Optional<TrackConnector> getToTrackConnector() {
        TimeInterval nextInterval = this.getNextTrainInterval();
        if (isNodeOwner()) {
            Node node = getOwnerAsNode();
            return nextInterval == null ? Optional.empty() :
                node.getConnectors().getForLineTrack((LineTrack) nextInterval.getTrack());
        } else {
            return nextInterval.getFromTrackConnector();
        }
    }

    /**
     * @return true in case it is inner node interval and both track connectors are
     *         on the same side of station
     */
    public boolean isDirectionChange() {
        if (this.isLineOwner()) {
            // line interval cannot be direction change
            return false;
        }
        return this.getFromTrackConnector()
                .map(c -> this.getToTrackConnector()
                        .map(c2 -> c.getOrientation() == c2.getOrientation()).orElse(false))
                .orElse(false);
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
        if (isAttached()) {
            throw new IllegalStateException("Time interval is already attached.");
        }
        owner.addTimeInterval(this);
        this.clearChanged();
    }

    public void addToOwnerWithoutCheck() {
        owner.addTimeInterval(this);
        this.clearChanged();
    }

    public void updateInOwner() {
        if (!isAttached()) {
            throw new IllegalStateException("Time interval is not attached.");
        }
        if (isChanged()) {
            owner.updateTimeInterval(this);
            clearChanged();
        }
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public boolean isNodeOwner() {
        return (owner instanceof Node);
    }

    public boolean isLineOwner() {
        return (owner instanceof Line);
    }

    public Node getOwnerAsNode() {
        return isNodeOwner() ? (Node) owner : null;
    }

    public Line getOwnerAsLine() {
        return isLineOwner() ? (Line) owner : null;
    }

    public boolean isTechnological() {
        return train.getTimeIntervalBefore() == this || train.getTimeIntervalAfter() == this;
    }

    public boolean isTechnologicalBefore() {
        return train.getTimeIntervalBefore() == this;
    }

    public boolean isTechnologicalAfter() {
        return train.getTimeIntervalAfter() == this;
    }

    public boolean isJoiningTrains() {
        return isTechnologicalAfter() && train.getNextJoinedTrain() != null;
    }

    public boolean isStop() {
        return isInnerStop() || isFirst() || isLast();
    }

    public boolean isInnerStop() {
        return isNodeOwner() && getLength() != 0 && !isTechnological();
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

    public TimeInterval getTrainInterval(int relativeIndex) {
        return train.getInterval(this, relativeIndex);
    }

    public TimeInterval getNextTrainInterval() {
        return this.getTrainInterval(1);
    }

    public TimeInterval getPreviousTrainInterval() {
        return this.getTrainInterval(-1);
    }

    public TimeIntervalCalculation getCalculation() {
        return calculation;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isAttached() {
        return train.isAttached();
    }

    public boolean isNotManagedFreight() {
    	return this.getAttributeAsBool(ATTR_NOT_MANAGED_FREIGHT);
    }

    public boolean isNoRegionCenterTransfer() {
        return this.getAttributeAsBool(ATTR_NO_REGION_CENTER_TRANSFER);
    }

    public LocalizedString getComment() {
        return this.getAttribute(ATTR_COMMENT, LocalizedString.class);
    }

    /**
     * @return if the node is center of regions and there is not
     *         <code>ATTR_NO_REGION_CENTER_TRANSFER</code> attribute present
     */
    public boolean isRegionCenterTransfer() {
        return this.isFreightTo() && this.getOwnerAsNode().isCenterOfRegions() && !this.isNoRegionCenterTransfer();
    }

    public boolean isFreightFrom() {
        return isFreightCommon() && (this.isFirst() || this.isInnerStop());
    }

    public boolean isFreightTo() {
        return isFreightCommon() && (this.isLast() || this.isInnerStop());
    }

    public boolean isFreight() {
        return isFreightCommon() && this.isStop();
    }

    public boolean isFreightConnection() {
        return !getTrain().getDiagram().getFreightNet().getTrainsFrom(this).isEmpty();
    }

    private boolean isFreightCommon() {
        return this.getTrain().isManagedFreight() && !this.isNotManagedFreight() && !this.isTechnological();
    }
}
