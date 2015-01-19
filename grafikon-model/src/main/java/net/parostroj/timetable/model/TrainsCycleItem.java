/*
 * TrainCycleItem.java
 *
 * Created on 15.9.2007, 19:48:09
 */
package net.parostroj.timetable.model;

import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Train cycle item.
 *
 * @author jub
 */
public class TrainsCycleItem implements TrainsCycleItemAttributes, AttributesHolder {

    private final Train train;
    private String comment;
    private final TrainsCycle cycle;
    private final TimeInterval from;
    private final TimeInterval to;

    private Attributes attributes;
    private AttributesListener attributesListener;

    public TrainsCycleItem(TrainsCycle cycle, Train train, String comment, TimeInterval from, TimeInterval to, Attributes attributes) {
        this.cycle = cycle;
        this.train = train;
        this.comment = comment;
        this.from = (train.getFirstInterval() != from) ? from : null;
        this.to = (train.getLastInterval() != to) ? to : null;
        this.setAttributes(attributes != null ? attributes : new Attributes());
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
        if (!ObjectsUtil.compareWithNull(comment, this.comment)) {
            String oldComment = this.comment;
            this.comment = comment;
            TrainsCycleEvent event = new TrainsCycleEvent(getCycle(), GTEventType.CYCLE_ITEM_UPDATED);
            event.setNewCycleItem(this);
            event.setOldCycleItem(this);
            event.setAttributeChange(new AttributeChange(ATTR_COMMENT, oldComment, comment));
            getCycle().fireEvent(event);
        }
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
    public void setAttributes(Attributes attributes) {
        if (this.attributes != null && attributesListener != null)
            this.attributes.removeListener(attributesListener);
        this.attributes = attributes;
        this.attributesListener = new AttributesListener() {

            @Override
            public void attributeChanged(Attributes attributes, AttributeChange change) {
                TrainsCycleEvent event = new TrainsCycleEvent(getCycle(), GTEventType.CYCLE_ITEM_UPDATED);
                event.setNewCycleItem(TrainsCycleItem.this);
                event.setOldCycleItem(TrainsCycleItem.this);
                event.setAttributeChange(change);
                getCycle().fireEvent(event);
            }
        };
        this.attributes.addListener(attributesListener);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
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
