package net.parostroj.timetable.gui.events;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Message for selection of time interval.
 *
 * @author jub
 */
public record IntervalSelectionMessage(TimeInterval interval) {
    @Override
    public String toString() {
        return interval != null ?
                String.format("IntervalMessage<%s,%s>", interval.getTrain(), interval) :
                "IntervalMessage<empty>";
    }
}
