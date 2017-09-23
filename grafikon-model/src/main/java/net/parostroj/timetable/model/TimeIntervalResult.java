package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.Set;

/**
 * Result for testing overlapping intervals.
 *
 * @author jub
 */
public final class TimeIntervalResult {

    /**
     * Test result status.
     *
     * @author jub
     */
    public enum Status {

        OK, OVERLAPPING
    }

    private final Status status;
    private final Set<TimeInterval> overlappingIntervals;

    /**
     * @param status status
     */
    public TimeIntervalResult(Status status) {
        this.status = status;
        this.overlappingIntervals = Collections.emptySet();
    }

    /**
     * @param status status
     * @param overlappingIntervals overlapping intervals
     */
    public TimeIntervalResult(Status status, Set<TimeInterval> overlappingIntervals) {
        this.status = status;
        this.overlappingIntervals = overlappingIntervals;
    }

    /**
     * @return the overlappingIntervals
     */
    public Set<TimeInterval> getOverlappingIntervals() {
        return overlappingIntervals;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", status, overlappingIntervals);
    }
}
