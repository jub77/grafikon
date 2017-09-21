package net.parostroj.timetable.utils;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Utility for time manipulation.
 *
 * @author jub
 */
public final class TimeUtil {

    private TimeUtil() {}

    /**
     * adjusts time between 0:00 and 23:59.
     *
     * @param time time
     * @return normalized time
     */
    public static int normalizeTime(int time) {
        int normalizedTime = time;
        while (!isNormalizedTime(normalizedTime)) {
            if (normalizedTime < 0) {
                normalizedTime += TimeInterval.DAY;
            } else if (normalizedTime >= TimeInterval.DAY) {
                normalizedTime -= TimeInterval.DAY;
            }
        }
        return normalizedTime;
    }

    /**
     * returns if the time is normalized.
     *
     * @param time time
     * @return normalized?
     */
    public static boolean isNormalizedTime(int time) {
        return time >= 0 && time < TimeInterval.DAY;
    }

    /**
     * Returns normalized time difference.
     *
     * @param startTime start time
     * @param endTime end time
     * @return normalized difference
     */
    public static int difference(int startTime, int endTime) {
        int start = normalizeTime(startTime);
        int end = normalizeTime(endTime);
        if (end < start) {
            end += TimeInterval.DAY;
        }
        return end - start;
    }

    /**
     * @param i1 first interval
     * @param i2 second interval
     * @return compares normalized starts of the intervals
     */
    public static int compareNormalizedStarts(TimeInterval i1, TimeInterval i2) {
        return Integer.compare(i1.getInterval().getNormalizedStart(), i2.getInterval().getNormalizedStart());
    }

    /**
     * @param i1 first interval
     * @param i2 second interval
     * @return compares normalized ends of the intervals
     */
    public static int compareNormalizedEnds(TimeInterval i1, TimeInterval i2) {
        return Integer.compare(i1.getInterval().getNormalizedEnd(), i2.getInterval().getNormalizedEnd());
    }
}
