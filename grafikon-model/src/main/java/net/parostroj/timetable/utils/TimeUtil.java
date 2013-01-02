package net.parostroj.timetable.utils;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Utility for time manipulation.
 *
 * @author jub
 */
public class TimeUtil {

	/**
     * adjusts time between 0:00 and 23:59.
     *
     * @param time time
     * @return normalized time
     */
    public static int normalizeTime(int time) {
        while (!isNormalizedTime(time)) {
            if (time < 0)
                time += TimeInterval.DAY;
            else if (time >= TimeInterval.DAY)
                time -= TimeInterval.DAY;
        }
        return time;
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
}
