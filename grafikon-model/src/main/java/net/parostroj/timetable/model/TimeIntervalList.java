package net.parostroj.timetable.model;

import java.util.*;

/**
 * Time interval list.
 *
 * @author jub
 */
public class TimeIntervalList extends ArrayList<TimeInterval> {

    /**
     * Default constructor.
     */
    public TimeIntervalList() {
    }

    /**
     * returns time interval relative to the one passed as an argument.
     *
     * @param i reference interval
     * @param relativeIndex relative position
     * @return target interval
     */
    public TimeInterval getInterval(TimeInterval i, int relativeIndex) {
        if (relativeIndex == 0) {
            return i;
        } else {
            int ind = this.indexOf(i) + relativeIndex;
            if (ind < 0 || ind >= this.size()) {
                return null;
            }
            return this.get(ind);
        }
    }

    /**
     * adds time interval to the list. It uses normalized times for sorting.
     *
     * @param interval time interval do be added
     */
    public void addIntervalForRouteSegment(TimeInterval interval) {
        // update overlapping intervals
        interval.setOverlappingIntervals(this.testIntervalForRouteSegmentOI(interval).getOverlappingIntervals());

        // update overlapping intervals in corresponding time intervals
        for (TimeInterval item : interval.getOverlappingIntervals()) {
            item.getOverlappingIntervals().add(interval);
        }

        int i = 0;
        for (TimeInterval item : this) {
            if (item.compareOpenNormalized(interval) == -1) {
                this.add(i, interval);
                return;
            }
            i++;
        }
        this.add(interval);
    }

    /**
     * adds time interval to the list. It doesn't check overlapping intervals.
     * It uses normalized times for sorting.
     *
     * @param interval time interval do be added
     */
    public void addIntervalForRouteSegmentWithoutCheck(TimeInterval interval) {
        int i = 0;
        for (TimeInterval item : this) {
            if (item.compareOpenNormalized(interval) == -1) {
                this.add(i, interval);
                return;
            }
            i++;
        }
        this.add(interval);
    }

    public void addIntervalByNormalizedStartTime(TimeInterval interval) {
        int i = 0;
        for (TimeInterval item : this) {
            if (item.getInterval().getNormalizedStart() >= interval.getInterval().getNormalizedStart()) {
                this.add(i,interval);
                return;
            }
            i++;
        }
        this.add(interval);
    }

    /**
     * adds time interval at the end of list. No testing performed. Usefull when
     * creating train.
     *
     * @param interval interval to be added
     */
    public void addIntervalLastForTrain(TimeInterval interval) {
        this.add(interval);
    }

    /**
     * removes time interval for specified train.
     *
     * @param interval time interval
     */
    public void removeIntervalForRouteSegment(TimeInterval interval) {
        ListIterator<TimeInterval> i = this.listIterator();
        while (i.hasNext()) {
            TimeInterval item = i.next();
            if (item == interval) {
                i.remove();
                break;
            }
        }

        // remove itself from other time intervals (overlapping)
        for (TimeInterval item : interval.getOverlappingIntervals()) {
            item.getOverlappingIntervals().remove(interval);
        }
    }

    /**
     * tests if specified time frame is available. It doesn't return ovelapping intervals.
     *
     * @param interval time interval to be tested
     * @return result
     */
    public TimeIntervalResult testIntervalForRouteSegment(TimeInterval interval) {
        for (TimeInterval item : this) {
            if (item.compareOpenNormalized(interval) == 0 && !item.equals(interval)) {
                return new TimeIntervalResult(TimeIntervalResult.Status.OVERLAPPING);
            }
        }
        return new TimeIntervalResult(TimeIntervalResult.Status.OK);
    }

    /**
     * tests if specified time interval is available to be added. It returns overlapping intervals.
     *
     * @param interval time interval to be tested
     * @return result
     */
    public TimeIntervalResult testIntervalForRouteSegmentOI(TimeInterval interval) {
        Set<TimeInterval> overlaps = null;
        TimeIntervalResult.Status status = TimeIntervalResult.Status.OK;

        for (TimeInterval item : this) {
            if (item.compareOpenNormalized(interval) == 0 && !item.equals(interval)) {
                if (status == TimeIntervalResult.Status.OK) {
                    status = TimeIntervalResult.Status.OVERLAPPING;
                    overlaps = new HashSet<TimeInterval>();
                }
                overlaps.add(item);
            }
        }

        return new TimeIntervalResult(status, overlaps);
    }

    public boolean updateInterval(TimeInterval interval) {
        return (interval.isNodeOwner()) ? this.updateNodeInterval(interval) : this.updateLineInterval(interval);
    }

    public boolean updateNodeInterval(TimeInterval interval) {
        if (!interval.isNodeOwner()) {
            throw new IllegalArgumentException("Node is not owner of the time interval.");
        }
        boolean changed = interval.isChanged();
        if (interval.isAttached()) {
            interval.updateInOwner();
        }
        return changed;
    }

    public boolean updateLineInterval(TimeInterval interval) {
        if (!interval.isLineOwner()) {
            throw new IllegalArgumentException("Line is not owner of the interval.");
        }
        // compute running time
        TimeIntervalCalculation calculation = interval.getCalculation();
        int computedSpeed = calculation.computeLineSpeed();
        int runnningTime = calculation.computeRunningTime();
        interval.setLength(runnningTime);
        interval.setSpeed(computedSpeed);
        boolean changed = interval.isChanged();
        if (interval.isAttached()) {
            interval.updateInOwner();
        }
        return changed;
    }
}
