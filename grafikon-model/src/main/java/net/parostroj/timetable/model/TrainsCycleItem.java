/*
 * TrainCycleItem.java
 *
 * Created on 15.9.2007, 19:48:09
 */
package net.parostroj.timetable.model;

import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.model.events.AttributeChange;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainsCycleEvent;

/**
 * Train cycle item.
 *
 * @author jub
 */
public class TrainsCycleItem {

    private final Train train;
    private String comment;
    private final TrainsCycle cycle;
    private final TimeInterval from;
    private final TimeInterval to;

    public TrainsCycleItem(TrainsCycle cycle, Train train, String comment, TimeInterval from, TimeInterval to) {
        this.cycle = cycle;
        this.train = train;
        this.comment = comment;
        this.from = (train.getFirstInterval() != from) ? from : null;
        this.to = (train.getLastInterval() != to) ? to : null;
    }

    public boolean containsInterval(TimeInterval interval) {
        boolean in = false;
        for (TimeInterval currentInterval : train.getTimeIntervalList()) {
            if (getFromInterval() == currentInterval)
                in = true;
            if (in && interval == currentInterval)
                return true;
            if (getToInterval() == currentInterval)
                in = false;
        }
        return false;
    }

    public List<TimeInterval> getIntervals() {
        List<TimeInterval> intervals = new LinkedList<TimeInterval>();
        boolean in = false;
        for (TimeInterval currentInterval : train.getTimeIntervalList()) {
            if (getFromInterval() == currentInterval)
                in = true;
            if (in)
                intervals.add(currentInterval);
            if (getToInterval() == currentInterval)
                in = false;
        }
        return intervals;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        String oldComment = this.comment;
        this.comment = comment;
        TrainsCycleEvent event = new TrainsCycleEvent(getCycle(), GTEventType.CYCLE_ITEM_UPDATED);
        event.setNewCycleItem(this);
        event.setOldCycleItem(this);
        event.setAttributeChange(new AttributeChange("comment", oldComment, comment));
        getCycle().fireEvent(event);
    }

    public Train getTrain() {
        return train;
    }

    public TrainsCycle getCycle() {
        return cycle;
    }

    /**
     * @return from time interval
     */
    public TimeInterval getFrom() {
        return from;
    }

    /**
     * @return to interval
     */
    public TimeInterval getTo() {
        return to;
    }

    /**
     * @return always returns from time interval (if not specified then firt interval of the train)
     */
    public TimeInterval getFromInterval() {
        if (from != null) {
            return from;
        } else {
            return train.getFirstInterval();
        }
    }

    /**
     * @return always returns to interval (if not specified then last interval of the train)
     */
    public TimeInterval getToInterval() {
        if (to != null) {
            return to;
        } else {
            return train.getLastInterval();
        }
    }

    public int getStartTime() {
        return this.getFromInterval().getEnd();
    }

    public int getEndTime() {
        // TODO normalized ??
        return this.getToInterval().getStart();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TrainsCycleItem[");
        builder.append(train).append(',');
        if (cycle != null) {
            builder.append(cycle.getName()).append(',');
        }
        builder.append(getFromInterval().getOwner()).append(',');
        builder.append(getToInterval().getOwner()).append(']');
        return builder.toString();
    }
}
