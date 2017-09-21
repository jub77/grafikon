package net.parostroj.timetable.model;

import net.parostroj.timetable.utils.TimeUtil;

/**
 * Time interval implementation - normalized.
 *
 * @author jub
 */
public final class IntervalNormalizedImpl extends IntervalImpl {

    // non-normalized interval over midnight
    private final Interval overMidnight;

    IntervalNormalizedImpl(int start, int end) {
        super(start, end);
        if (!TimeUtil.isNormalizedTime(start)) {
            throw new IllegalArgumentException("Start is not normalized");
        }
        // over midnight
        if (TimeUtil.isNormalizedTime(start) && !TimeUtil.isNormalizedTime(end)) {
            // limited implementation of interval - it doesn't support a lot of methods
            overMidnight = new IntervalNonNormalizedImpl(start - TimeInterval.DAY, end - TimeInterval.DAY, this);
        } else {
            overMidnight = null;
        }
    }

    @Override
    public int getNormalizedStart() {
        return start;
    }

    @Override
    public int getNormalizedEnd() {
        return end;
    }

    @Override
    public Interval normalize() {
        return this;
    }

    @Override
    public boolean isNormalized() {
        return true;
    }

    @Override
    public boolean isOverMidnight() {
        return overMidnight != null;
    }

    @Override
    public boolean isOverThreshold(int threshold) {
        return threshold == 0 ? this.isOverMidnight()
                : (start < threshold && end >= threshold)
                        || (start < threshold + TimeInterval.DAY && end >= threshold + TimeInterval.DAY);
    }

    @Override
    public Interval getNonNormalizedIntervalOverMidnight() {
        return overMidnight;
    }

    @Override
    public Interval getComplementatyIntervalOverThreshold(int threshold) {
        if (threshold == 0) {
            return this.getNonNormalizedIntervalOverMidnight();
        } else if (this.isOverThreshold(threshold)) {
            int s = start < threshold ? start + TimeInterval.DAY : start - TimeInterval.DAY;
            int e = s + this.getLength();
            return new IntervalNonNormalizedImpl(s, e, this);
        } else {
            return null;
        }
    }
}
