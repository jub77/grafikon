package net.parostroj.timetable.actions;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Tests for freight manipulations.
 *
 * @author jub
 */
public class FreightHelper {

    public static boolean isFreightFrom(TimeInterval interval) {
        return interval.isNodeOwner() && (interval.isFirst() || interval.getLength() > 0)
                && !interval.getAttributes().getBool(TimeInterval.ATTR_NOT_MANAGED_FREIGHT);
    }

    public static boolean isFreightTo(TimeInterval interval) {
        return interval.isNodeOwner() && (interval.isLast() || interval.getLength() > 0)
                && !interval.getAttributes().getBool(TimeInterval.ATTR_NOT_MANAGED_FREIGHT);
    }

    public static Iterable<TimeInterval> getNodeIntervalsFreightFrom(Iterable<TimeInterval> i) {
        return new FilteredIterable<TimeInterval>(i, new FilteredIterable.Filter<TimeInterval>() {
            @Override
            public boolean is(TimeInterval instance) {
                return isFreightFrom(instance);
            }
        });
    }

    public static Iterable<TimeInterval> getNodeIntervalsFreightTo(Iterable<TimeInterval> i) {
        return new FilteredIterable<TimeInterval>(i, new FilteredIterable.Filter<TimeInterval>() {
            @Override
            public boolean is(TimeInterval instance) {
                return isFreightTo(instance);
            }
        });
    }

    public static Iterable<TimeInterval> getNodeIntervalsWithFreight(Iterable<TimeInterval> i, final TimeInterval from) {
        return new FilteredIterable<TimeInterval>(i, new FilteredIterable.Filter<TimeInterval>() {

            boolean after = false;

            @Override
            public boolean is(TimeInterval instance) {
                if (after) {
                    return FreightHelper.isFreightTo(instance);
                } else {
                    after = instance == from;
                    return false;
                }
            }
        });
    }
}
