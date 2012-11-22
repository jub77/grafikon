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
     * returns time interval for specified train. If the time interval for specified train
     * doesn't exist, it returns <code>null</code>.
     *
     * @param train train
     * @return time interval
     */
    public TimeInterval getTimeInterval(Train train) {
        for (TimeInterval interval : this) {
            if (interval.getTrain().equals(train)) {
                return interval;
            }
        }
        return null;
    }

    public TimeInterval getIntervalBefore(TimeInterval i) {
        int ind = this.indexOf(i);
        if (ind < 1)
            return null;
        return this.get(ind - 1);
    }

    public TimeInterval getIntervalAfter(TimeInterval i) {
        int ind = this.indexOf(i);
        if (ind == -1 || ind > (this.size() - 2))
            return null;
        return this.get(ind + 1);
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

    /**
     * shifts the whole list of time intervals. Can be used only for train time
     * interval list.
     *
     * @param timeShift amount of time to be shifted
     */
    public void shift(int timeShift) {
        this.shiftFrom(0, timeShift);
    }

    /**
     * moves the whole list to specified starting point. Can be used only for train time
     * interval list.
     *
     * @param time new starting time
     */
    public void move(int time) {
        this.moveFrom(0, time);
    }

    public void moveFrom(int index, int time) {
        TimeInterval interval = this.get(index);
        int shift = time - interval.getStart();
        this.shiftFrom(index, shift);
    }

    public void shiftFrom(int index, int timeShift) {
        ListIterator<TimeInterval> i = this.listIterator(index);
        while (i.hasNext()) {
            TimeInterval item = i.next();
            item.shift(timeShift);
            if (item.isAttached())
                item.updateInOwner();
        }
    }

    public void shiftFrom(TimeInterval interval, int timeShift) {
        int i = this.indexOf(interval);
        if (i == -1)
            throw new IllegalArgumentException("Interval is not part of the list.");
        this.shiftFrom(i, timeShift);
    }

    public int computeFromSpeed(TimeInterval interval) {
        if (!interval.isLineOwner())
            throw new IllegalArgumentException("Cannot find speed for node.");
        int i = this.indexOf(interval);
        if (i == -1)
            throw new IllegalArgumentException("Interval is not part of the list.");
        return this.computeFromSpeed(interval, i);
    }

    public int computeToSpeed(TimeInterval interval) {
        if (!interval.isLineOwner())
            throw new IllegalArgumentException("Cannot find speed for node.");
        int i = this.indexOf(interval);
        if (i == -1)
            throw new IllegalArgumentException("Interval is not part of the list.");
        return this.computeToSpeed(interval, i);
    }

    public int computeFromSpeed(TimeInterval interval, int i) {
        // previous node is stop - first node or node has not null time
        if ((i - 1) == 0 || this.get(i - 1).getLength() != 0)
            return 0;
        else {
            // check speed of previous line
            return this.get(i - 2).getSpeed();
        }
    }

    public int computeToSpeed(TimeInterval interval, int i) {
        // next node is stop - last node or node has not null time
        if ((i + 1) == (this.size() - 1) || this.get(i + 1).getLength() != 0)
            return 0;
        else {
            // check speed of previous line
            return this.get(i + 2).getSpeed();
        }
    }

    public void updateInterval(TimeInterval interval) {
        int i = this.indexOf(interval);
        if (i == -1)
            throw new IllegalArgumentException("Interval is not part of the list.");
        this.updateInterval(interval, i);
    }

    public void updateInterval(TimeInterval interval, int i) {
        if (interval.isNodeOwner()) {
            this.updateNodeInterval(interval, i);
        } else {
            this.updateLineInterval(interval, i);
        }
    }

    public void updateNodeInterval(TimeInterval interval, int i) {
        if (!interval.isNodeOwner())
            throw new IllegalArgumentException("Node is not owner of the time interval.");
        if (interval.isAttached())
            interval.updateInOwner();
    }

    public void updateLineInterval(TimeInterval interval, int i) {
        if (!interval.isLineOwner())
            throw new IllegalArgumentException("Line is not owner of the interval.");
        // compute running time
        int runnningTime = interval.getOwnerAsLine().computeRunningTime(
                interval.getTrain(), interval.getStart(), interval.getSpeed(),
                this.computeFromSpeed(interval, i),
                this.computeToSpeed(interval, i), interval.getAddedTime());
        interval.setLength(runnningTime);
        if (interval.isAttached())
            interval.updateInOwner();
    }
}
