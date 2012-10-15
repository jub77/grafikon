package net.parostroj.timetable.model;

/**
 * Time interval interface..
 *
 * @author jub
 */
public interface Interval {

    public abstract int getEnd();

    public abstract int getStart();

    public abstract int getNormalizedStart();

    public abstract int getNormalizedEnd();

    public abstract int getLength();

    public abstract Interval normalize();

    public abstract boolean isNormalized();

    public abstract boolean isOverMidnight();

    public abstract int compareOpen(Interval o);

    public abstract int compareOpenNormalized(Interval o);

    public abstract int compareClosed(Interval o);

    public abstract int compareClosedNormalized(Interval o);

    public abstract Interval getNonNormalizedIntervalOverMidnight();
}
