package net.parostroj.timetable.model;

import java.util.Set;

/**
 * Result for testing overlapping intervals.
 *
 * @author jub
 */
public class TimeIntervalResult {

    /**
     * Test result status.
     *
     * @author jub
     */
    public enum Status {

        OK, OVERLAPPING
    }

    private Status status;

    private Set<TimeInterval> overlappingIntervals;

    /**
     * Constructor.
     *
     * @param status status
     */
    public TimeIntervalResult(Status status) {
        this.status = status;
    }

    /**
     * Constructor.
     *
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
     * @param overlappingIntervals the overlappingIntervals to set
     */
    public void setOverlappingIntervals(Set<TimeInterval> overlappingIntervals) {
        this.overlappingIntervals = overlappingIntervals;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }
}
