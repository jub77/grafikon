package net.parostroj.timetable.model;

import net.parostroj.timetable.utils.TimeUtil;

/**
 * Interval factory.
 *
 * @author jub
 */
public class IntervalFactory {

    public static Interval createInterval(int start, int end) {
        int length = end - start;
        if (length >= TimeInterval.DAY)
            throw new IllegalArgumentException("Interval cannot be longer or equal than a day.");
        if (TimeUtil.isNormalizedTime(start))
            return new IntervalNormalizedImpl(start, end);
        else
            return new IntervalNonNormalizedImpl(start, end);
    }
}
