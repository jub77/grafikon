package net.parostroj.timetable.model;

/**
 * Time interval interface..
 *
 * @author jub
 */
public interface Interval {

    int getEnd();

    int getStart();

    int getNormalizedStart();

    int getNormalizedEnd();

    int getLength();

    Interval normalize();

    boolean isNormalized();

    boolean isOverMidnight();

    boolean isOverThreshold(int threshold);

    int compareOpen(Interval o);

    int compareOpenNormalized(Interval o);

    int compareClosed(Interval o);

    int compareClosedNormalized(Interval o);

    Interval getNonNormalizedIntervalOverMidnight();

    Interval getComplementatyIntervalOverThreshold(int threshold);
}
