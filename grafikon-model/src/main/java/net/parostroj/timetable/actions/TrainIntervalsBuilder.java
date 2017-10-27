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

    private static final Logger log = LoggerFactory.getLogger(TrainIntervalsBuilder.class);
    private final Train train;
    private TimeInterval lastInterval;
    private final int startTime;
    private boolean finished;

    public TrainIntervalsBuilder(Train train, int startTime) {
        this.train = train;
        this.lastInterval = null;
        this.startTime = startTime;
        this.finished = false;
    }

    public void addNode(String intervalId, Node node, NodeTrack track, int stop, Attributes attributes) {
        String newIntervalId = intervalId;
        if (newIntervalId == null) {
            log.warn("Adding interval with not specified id (fix - generated): {}", node);
            newIntervalId = IdGenerator.getInstance().getId();
        }
        if (finished) {
            throw new IllegalStateException("Cannot add node time interval to finished train.");
        }
        if (lastInterval == null) {
            // create first time interval
            lastInterval = new TimeInterval(newIntervalId, train, node, startTime, startTime, track);
        } else {
            if (lastInterval.getOwnerAsLine() == null) {
                throw new IllegalStateException("Last interval owner was not line.");
            }
            lastInterval = new TimeInterval(newIntervalId,
                    train, node, 0, stop, track);
        }
        lastInterval.getAttributes().add(attributes);
        train.addInterval(lastInterval);
    }

    public void addLine(String intervalId, Line line, LineTrack track, Integer speed, int addedTime, Attributes attributes) {
        String newIntervalId = intervalId;
        if (newIntervalId == null) {
            log.warn("Adding interval with not specified id (fix - generated): {}", line);
            newIntervalId = IdGenerator.getInstance().getId();
        }
        if (finished) {
            throw new IllegalStateException("Cannot add line time interval to finished train.");
        }
        if (lastInterval == null || lastInterval.getOwnerAsNode() == null) {
            throw new IllegalStateException("Last interval owner was not node.");
        }

        lastInterval = new TimeInterval(
                newIntervalId, train, line, 0, 0, speed,
                lastInterval.getOwnerAsNode() == line.getFrom() ?
                        TimeIntervalDirection.FORWARD : TimeIntervalDirection.BACKWARD,
                track, addedTime);
        lastInterval.getAttributes().add(attributes);
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
