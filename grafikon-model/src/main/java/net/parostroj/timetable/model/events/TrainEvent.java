package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Train event.
 * 
 * @author jub
 */
public class TrainEvent extends GTEvent<Train> {

    public static enum TimeIntervalListType {
        MOVED, STOP_TIME, SPEED, TRACK, RECALCULATE, ADDED;
    }

    private TimeIntervalListType timeIntervalListType;
    private TrainsCycleItem cycleItem;
    private int intervalChangeStart;
    private int changedInterval;

    public TrainEvent(Train train, GTEventType type) {
        super(train, type);
    }

    public TrainEvent(Train train, AttributeChange attributeChange) {
        this(train, GTEventType.ATTRIBUTE);
        setAttributeChange(attributeChange);
    }

    public TrainEvent(Train train, GTEventType type, TrainsCycleItem cycleItem) {
        this(train, type);
        this.cycleItem = cycleItem;
    }

    public TrainEvent(Train train, AttributeChange attributeChange, int changedInterval) {
        this(train, GTEventType.TIME_INTERVAL_ATTRIBUTE);
        setAttributeChange(attributeChange);
        this.changedInterval = changedInterval;
    }

    public TrainEvent(Train train, TimeIntervalListType type, int changedInterval, int intervalChangeStart) {
        this(train, GTEventType.TIME_INTERVAL_LIST);
        this.timeIntervalListType = type;
        this.changedInterval = changedInterval;
        this.intervalChangeStart = intervalChangeStart;
    }

    public TrainEvent(Train train, TimeIntervalListType type, int changedInterval) {
        this(train, type, changedInterval, 0);
    }

    public TrainsCycleItem getCycleItem() {
        return cycleItem;
    }

    public int getChangedInterval() {
        return changedInterval;
    }

    public int getIntervalChangeStart() {
        return intervalChangeStart;
    }

    public TimeIntervalListType getTimeIntervalListType() {
        return timeIntervalListType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TrainEvent[");
        builder.append(getSource()).append(',');
        builder.append(getType());
        if (getType() == GTEventType.ATTRIBUTE) {
            builder.append(',').append(getAttributeChange());
        }
        if (cycleItem != null) {
            builder.append(',').append(cycleItem);
        }
        builder.append(',').append(changedInterval).append(',').append(intervalChangeStart);
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
