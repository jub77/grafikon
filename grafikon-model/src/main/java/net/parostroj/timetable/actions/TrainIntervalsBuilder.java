package net.parostroj.timetable.actions;

import java.util.ArrayList;
import java.util.List;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for creating trains.
 *
 * @author jub
 */
public class TrainIntervalsBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TrainIntervalsBuilder.class.getName());
    private final Train train;
    private TimeInterval lastInterval;
    private final int startTime;
    private boolean finished;
    private final List<TimeInterval> timeIntervals;

    public TrainIntervalsBuilder(TrainDiagram diagram, Train train, int startTime) {
        this.train = train;
        this.lastInterval = null;
        this.startTime = startTime;
        this.finished = false;
        this.timeIntervals = new ArrayList<TimeInterval>();
    }

    public void addNode(String intervalId, Node node, NodeTrack track, int stop, Attributes attributes) {
        if (intervalId == null) {
            LOG.warn("Adding interval with not specified id (fix - generated): {}", node);
            intervalId = IdGenerator.getInstance().getId();
        }
        if (finished) {
            throw new IllegalStateException("Cannot add node time interval to finished train.");
        }
        if (lastInterval == null) {
            // create first time interval
            lastInterval = new TimeInterval(intervalId, train, node, startTime, startTime, track);
        } else {
            if (lastInterval.getOwner().asLine() == null) {
                throw new IllegalStateException("Last interval owner was not line.");
            }
            lastInterval = new TimeInterval(intervalId,
                    train, node, 0, stop, track);
        }
        lastInterval.setAttributes(attributes);
        timeIntervals.add(lastInterval);
    }

    public void addLine(String intervalId, Line line, LineTrack track, int speed, int addedTime, Attributes attributes) {
        if (intervalId == null) {
            LOG.warn("Adding interval with not specified id (fix - generated): {}", line);
            intervalId = IdGenerator.getInstance().getId();
        }
        if (finished) {
            throw new IllegalStateException("Cannot add line time interval to finished train.");
        }
        if (lastInterval == null || lastInterval.getOwner().asNode() == null) {
            throw new IllegalStateException("Last interval owner was not node.");
        }

        lastInterval = new TimeInterval(
                intervalId, train, line, 0, 0, speed,
                lastInterval.getOwner().asNode() == line.getFrom() ? TimeIntervalDirection.FORWARD : TimeIntervalDirection.BACKWARD,
                track, addedTime);
        lastInterval.setAttributes(attributes);
        timeIntervals.add(lastInterval);
    }

    public void finish() {
        if (finished) {
            throw new IllegalStateException("Cannot finish already finished train.");
        }

        // finish train
        int i = 0;
        TimeInterval createdInterval;
        int time = this.startTime;

        for (TimeInterval interval : timeIntervals) {
            if (interval.isNodeOwner()) {
                // handle node
                Node node = interval.getOwnerAsNode();
                createdInterval = node.createTimeInterval(
                        interval.getId(), train, time,
                        interval.getLength(), null);
            } else {
                // handle line
                Line line = interval.getOwnerAsLine();
                createdInterval = line.createTimeInterval(
                        interval.getId(), train, time,
                        interval.getDirection(), interval.getSpeed(),
                        this.computeFromSpeed(interval, timeIntervals, i),
                        this.computeToSpeed(interval, timeIntervals, i), interval.getAddedTime(), null);
            }

            // set track and attributes
            createdInterval.setTrack(interval.getTrack());
            createdInterval.setAttributes(new Attributes(interval.getAttributes()));

            // add created interval to train and set current time
            time = createdInterval.getEnd();
            train.addInterval(createdInterval);

            i++;
        }

        finished = true;
    }

    private int computeFromSpeed(TimeInterval interval, List<TimeInterval> intervals, int i) {
        if (!interval.isLineOwner())
            throw new IllegalArgumentException("Cannot find speed for node.");
        // previous node is stop - first node or node has not null time
        if ((i - 1) == 0 || intervals.get(i - 1).getLength() != 0)
            return 0;
        else {
            // check speed of previous line
            return intervals.get(i - 2).getSpeed();
        }
    }

    private int computeToSpeed(TimeInterval interval, List<TimeInterval> intervals, int i) {
        if (!interval.isLineOwner())
            throw new IllegalArgumentException("Cannot find speed for node.");
        // next node is stop - last node or node has not null time
        if ((i + 1) == (intervals.size() - 1) || intervals.get(i + 1).getLength() != 0)
            return 0;
        else {
            // check speed of previous line
            return intervals.get(i + 2).getSpeed();
        }
    }
}
