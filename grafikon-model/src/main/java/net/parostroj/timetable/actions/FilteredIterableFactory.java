package net.parostroj.timetable.actions;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Factory for some filtered iterables.
 *
 * @author jub
 */
public class FilteredIterableFactory {

    public static Iterable<TimeInterval> getNodeIntervalsFreightFrom(Iterable<TimeInterval> i) {
        return new FilteredIterable<TimeInterval>(i, new FilteredIterable.Filter<TimeInterval>() {
            @Override
            public boolean is(TimeInterval instance) {
                return (instance.isLast() || instance.getLength() > 0) && instance.isNodeOwner();
            }
        });
    }

    public static Iterable<TimeInterval> getNodeIntervalsFreightTo(Iterable<TimeInterval> i) {
        return new FilteredIterable<TimeInterval>(i, new FilteredIterable.Filter<TimeInterval>() {
            @Override
            public boolean is(TimeInterval instance) {
                return (instance.isFirst() || instance.getLength() > 0) && instance.isNodeOwner();
            }
        });
    }
}
