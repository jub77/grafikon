package net.parostroj.timetable.gui.utils;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Message for selection of time interval.
 *
 * @author jub
 */
public class IntervalSelectionMessage {

    private final TimeInterval interval;

    public IntervalSelectionMessage(TimeInterval interval) {
        this.interval = interval;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return interval != null ?
                String.format("IntervalMessage<%s,%s>", interval.getTrain(), interval) :
                "IntervalMessage<empty>";
    }
}
