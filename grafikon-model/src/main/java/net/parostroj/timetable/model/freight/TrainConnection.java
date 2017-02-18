package net.parostroj.timetable.model.freight;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

/**
 * Connection from node to node with specific train.
 *
 * @author jub
 */
public interface TrainConnection extends Connection<TimeInterval, TimeInterval> {

    default Train getTrain() {
        return getFrom().getTrain();
    }

    default int getStartTime() {
        TimeInterval interval = getFrom();
        return interval.isNodeOwner() ? interval.getEnd() : interval.getStart();
    }

    default int getEndTime() {
        TimeInterval interval = getTo();
        return interval.isNodeOwner() ? interval.getStart() : interval.getEnd();
    }
}
