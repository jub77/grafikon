package net.parostroj.timetable.model;

import net.parostroj.timetable.utils.TimeConverter;

/**
 * Time interval implementation - normalized.
 * 
 * @author jub
 */
final public class IntervalNormalizedImpl extends IntervalImpl {
    
    // non-normalized interval over midnight
    private final Interval overMidnight;

    IntervalNormalizedImpl(int start, int end) {
        super(start, end);
        if (!TimeConverter.isNormalizedTime(start))
            throw new IllegalArgumentException("Start is not normalized.");
        // over midnight
        if (TimeConverter.isNormalizedTime(start) && !TimeConverter.isNormalizedTime(end)) {
            // limited implementation of interval - it doesn't support a lot of methods
            overMidnight = new IntervalImpl(start - TimeInterval.DAY, end - TimeInterval.DAY);
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
    public Interval getNonNormalizedIntervalOverMidnight() {
        return overMidnight;
    }
}
