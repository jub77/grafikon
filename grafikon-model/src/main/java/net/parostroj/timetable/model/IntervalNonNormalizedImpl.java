package net.parostroj.timetable.model;

import net.parostroj.timetable.utils.TimeUtil;

/**
 * Time interval implementation - non normalized.
 *
 * @author jub
 */
final public class IntervalNonNormalizedImpl extends IntervalImpl {

    private final Interval normalized;

    IntervalNonNormalizedImpl(int start, int end) {
        super(start, end);
        if (TimeUtil.isNormalizedTime(start))
            throw new IllegalArgumentException("Start is normalized.");
        int normalizedStart = TimeUtil.normalizeTime(start);
        int computedEnd = normalizedStart + getLength();
        normalized = IntervalFactory.createInterval(normalizedStart, computedEnd);
    }

    @Override
    public int getNormalizedStart() {
        return normalized.getStart();
    }

    @Override
    public int getNormalizedEnd() {
        return normalized.getEnd();
    }

    @Override
    public Interval normalize() {
        return normalized;
    }

    @Override
    public boolean isNormalized() {
        return false;
    }

    @Override
    public boolean isOverMidnight() {
        return normalized.isOverMidnight();
    }

    @Override
    public Interval getNonNormalizedIntervalOverMidnight() {
        return normalized.getNonNormalizedIntervalOverMidnight();
    }
}
