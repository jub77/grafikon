package net.parostroj.timetable.model;

/**
 * This implementation supports only getting of limits.
 *
 * @author jub
 */
public abstract class IntervalImpl implements Interval {

    protected final int start;
    protected final int end;

    IntervalImpl(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public abstract int getNormalizedStart();

    @Override
    public abstract int getNormalizedEnd();

    @Override
    public int getLength() {
        return end - start;
    }

    @Override
    public abstract Interval normalize();

    @Override
    public abstract boolean isNormalized();

    @Override
    public abstract boolean isOverMidnight();

    @Override
    public abstract boolean isOverThreshold(int threshold);

    @Override
    public int compareOpen(Interval o) {
        if (o.getEnd() < this.getStart()) {
            return -1;
        }
        if (o.getStart() > this.getEnd()) {
            return 1;
        }
        return 0;
    }

    @Override
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

    @Override
    public int compareClosed(Interval o) {
        if (o.getEnd() <= this.getStart()) {
            return -1;
        }
        if (o.getStart() >= this.getEnd()) {
            return 1;
        }
        return 0;
    }

    @Override
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

    @Override
    public abstract Interval getNonNormalizedIntervalOverMidnight();

    @Override
    public abstract Interval getComplementatyIntervalOverThreshold(int threshold);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + end;
        result = prime * result + start;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Interval))
            return false;
        Interval other = (Interval) obj;
        if (getEnd() != other.getEnd())
            return false;
        if (getStart() != other.getStart())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", getStart(), getEnd());
    }
}
