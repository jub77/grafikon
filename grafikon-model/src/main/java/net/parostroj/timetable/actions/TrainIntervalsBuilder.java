package net.parostroj.timetable.actions;

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

    public TrainIntervalsBuilder(TrainDiagram diagram, Train train, int startTime) {
        this.train = train;
        this.lastInterval = null;
        this.startTime = startTime;
        this.finished = false;
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
        train.addInterval(lastInterval);
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
        train.addInterval(lastInterval);
    }

    public void finish() {
        if (finished) {
            throw new IllegalStateException("Cannot finish already finished train.");
        }

        // finish train
        train.recalculate();

        finished = true;
    }
}
