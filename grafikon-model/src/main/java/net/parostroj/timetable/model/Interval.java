package net.parostroj.timetable.model;

import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.utils.Tuple;

/**
 * Time interval. It holds two values - start and end. Immutable object.
 *
 * @author jub
 */
final public class Interval {
    private final int start;
    private final int end;
    private final Tuple<Integer> normalized;
    private final Tuple<Integer> overMidnight;

    public Interval(TimeInterval timeInterval) {
        this(timeInterval.getStart(), timeInterval.getEnd());
    }

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
        int length = end - start;
        if (length >= TimeInterval.DAY)
            throw new IllegalArgumentException("Interval cannot be longer or equal than a day.");
        int normalizedStart = start;
        int computedEnd = end;
        // compute normalized
        if (!TimeConverter.isNormalizedTime(start)) {
            normalizedStart = TimeConverter.normalizeTime(start);
            computedEnd = normalizedStart + length;
            normalized = new Tuple<Integer>(normalizedStart, computedEnd);
        } else {
            normalized = null;
        }
        // over midnight
        if (TimeConverter.isNormalizedTime(normalizedStart) && !TimeConverter.isNormalizedTime(computedEnd)) {
            overMidnight = new Tuple<Integer>(normalizedStart - TimeInterval.DAY, computedEnd - TimeInterval.DAY);
        } else {
            overMidnight = null;
        }
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public int getNormalizedStart() {
        return normalized != null ? normalized.first : start;
    }

    public int getNormalizedEnd() {
        return normalized != null ? normalized.second : end;
    }

    public int getLength() {
        return end - start;
    }

    public Interval normalize() {
        if (normalized == null)
            return this;
        else {
            return new Interval(normalized.first, normalized.second);
        }
    }

    /**
     * The interval is normalized if the start time of the interval is
     * within bounds 0 - (DAY - 1).
     *
     * @return if the interval is normalized
     */
    public boolean isNormalized() {
        return normalized == null;
    }

    /**
     * @return if the interval is normalized and over midnight
     */
    public boolean isNormalizedOverMidnight() {
        return normalized == null && overMidnight != null;
    }

    /**
     * @return if the interval is normalized and not over midnight
     */
    public boolean isNormalizedNotOverMidnight() {
        return normalized == null && overMidnight == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Interval other = (Interval) obj;
        if (this.start != other.start) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.start;
        hash = 67 * hash + this.end;
        return hash;
    }

    public int compareOpen(Interval o) {
        if (o.getEnd() < this.getStart()) {
            return -1;
        }
        if (o.getStart() > this.getEnd()) {
            return 1;
        }
        return 0;
    }

    public int compareOpenNormalized(Interval o) {
        Interval tNormalized = normalize();
        Interval oNormalized = o.normalize();
        Interval tMidnight = tNormalized.getNonNormalizedIntervalOverMidnight();
        Interval oMidnight = oNormalized.getNonNormalizedIntervalOverMidnight();

        if (tMidnight != null) {
            if (tMidnight.compareOpen(oNormalized) == 0)
                return 0;
            if (oMidnight != null && tMidnight.compareOpen(oMidnight) == 0)
                return 0;
        }

        if (oMidnight != null) {
            if (tNormalized.compareOpen(oMidnight) == 0)
                return 0;
        }

        // if not overlapped then compare the ones with normalized start time
        // (always the first ones)
        return tNormalized.compareOpen(oNormalized);
    }

    public int compareClosed(Interval o) {
        if (o.getEnd() <= this.getStart()) {
            return -1;
        }
        if (o.getStart() >= this.getEnd()) {
            return 1;
        }
        return 0;
    }

    public int compareClosedNormalized(Interval o) {
        Interval tNormalized = normalize();
        Interval oNormalized = o.normalize();
        Interval tMidnight = tNormalized.getNonNormalizedIntervalOverMidnight();
        Interval oMidnight = oNormalized.getNonNormalizedIntervalOverMidnight();

        if (tMidnight != null) {
            if (tMidnight.compareClosed(oNormalized) == 0)
                return 0;
            if (oMidnight != null && tMidnight.compareClosed(oMidnight) == 0)
                return 0;
        }

        if (oMidnight != null) {
            if (tNormalized.compareClosed(oMidnight) == 0)
                return 0;
        }

        // if not overlapped then compare the ones with normalized start time
        // (always the first ones)
        return tNormalized.compareClosed(oNormalized);
    }

    public Interval getNonNormalizedIntervalOverMidnight() {
        return overMidnight != null ?
            new Interval(overMidnight.first, overMidnight.second) :
            null;
    }
}
