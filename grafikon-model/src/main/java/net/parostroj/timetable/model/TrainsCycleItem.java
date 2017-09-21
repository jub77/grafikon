/*
 * TrainCycleItem.java
 *
 * Created on 15.9.2007, 19:48:09
 */
package net.parostroj.timetable.model;

import java.util.List;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.CollectionUtils;
import net.parostroj.timetable.utils.TimeUtil;

/**
 * Train cycle item.
 *
 * @author jub
 */
public class TrainsCycleItem implements TrainsCycleItemAttributes, AttributesHolder {

    private final Train train;
    private final TrainsCycle cycle;
    private final TimeInterval from;
    private final TimeInterval to;

    private final Attributes attributes;

    public TrainsCycleItem(TrainsCycle cycle, Train train, LocalizedString comment, TimeInterval from, TimeInterval to) {
        this.cycle = cycle;
        this.train = train;
        this.from = (train.getFirstInterval() != from) ? from : null;
        this.to = (train.getLastInterval() != to) ? to : null;
        this.attributes = new Attributes((attrs, change) -> getCycle().fireEvent(new Event(getCycle(), TrainsCycleItem.this, change)));
        this.attributes.setRemove(ATTR_COMMENT, comment);
    }

    public boolean containsInterval(TimeInterval interval) {
        return Iterators.contains(CollectionUtils.closedIntervalIterator(train.getTimeIntervalList().iterator(),
                getFromInterval(), getToInterval()), interval);
    }

    public List<TimeInterval> getIntervals() {
        return Lists.newArrayList(CollectionUtils.closedIntervalIterator(train.getTimeIntervalList().iterator(),
                getFromInterval(), getToInterval()));
    }

    public LocalizedString getComment() {
        return this.getAttribute(ATTR_COMMENT, LocalizedString.class);
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
        return from != null ? from : train.getFirstInterval();
    }

    /**
     * @return always returns to interval (if not specified then last interval of the train)
     */
    public TimeInterval getToInterval() {
        return to != null ? to : train.getLastInterval();
    }

    public Node getFromNode() {
        return this.getFromInterval().getOwnerAsNode();
    }

    public Node getToNode() {
        return this.getToInterval().getOwnerAsNode();
    }

    public int getStartTime() {
        return this.getFromInterval().getEnd();
    }

    public int getEndTime() {
        return this.getToInterval().getStart();
    }

    /**
     * @return normalized startTime in case start and end are not normalized
     */
    public int getNormalizedStartTime() {
        int startTime = this.getStartTime();
        return !TimeUtil.isNormalizedTime(startTime) ? TimeUtil.normalizeTime(startTime) : startTime;
    }

    /**
     * @return normalized endTime in case start and end are not normalized
     */
    public int getNormalizedEndTime() {
        int startTime = this.getStartTime();
        int endTime = this.getEndTime();
        return !TimeUtil.isNormalizedTime(startTime) ? TimeUtil.normalizeTime(endTime) : endTime;
    }

    public TrainsCycleItem getNextItem() {
        return getCycle().getNextItem(this);
    }

    public TrainsCycleItem getNextItemCyclic() {
        return getCycle().getNextItemCyclic(this);
    }

    public TrainsCycleItem getPreviousItem() {
        return getCycle().getPreviousItem(this);
    }

    public TrainsCycleItem getPreviousItemCyclic() {
        return getCycle().getPreviousItemCyclic(this);
    }

    public Integer getSetupTime() {
        return getAttribute(ATTR_SETUP_TIME, Integer.class);
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
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
