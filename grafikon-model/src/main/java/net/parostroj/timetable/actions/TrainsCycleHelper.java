package net.parostroj.timetable.actions;

import java.util.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.Tuple;

/**
 * Class for managing lists of trains cycle items.
 * 
 * @author jub
 */
public class TrainsCycleHelper {

    private final static TrainsCycleHelper instance = new TrainsCycleHelper();

    private TrainsCycleHelper() {
    }

    public static TrainsCycleHelper getHelper() {
        // currently one type of helper for all types
        return instance;
    }

    /**
     * adds trains cycle item to the list.
     *
     * @param timeIntervalList time interval list
     * @param items list of trains cycle items
     * @param item item to be added
     * @param overlapping of the overlapping is allowed
     */
    public void addCycleItem(List<TimeInterval> timeIntervalList, List<TrainsCycleItem> items, TrainsCycleItem item, boolean overlapping) {
        if (!testAddCycle(timeIntervalList, items, item, null, overlapping))
            throw new IllegalArgumentException("Overlapping item.");
        ListIterator<TrainsCycleItem> i = items.listIterator();
        TrainsCycleItem current = getNext(i, null);
        for (TimeInterval interval : timeIntervalList) {
            if (interval.isNodeOwner()) {
                if (current != null && interval == current.getFromInterval())
                    current = getNext(i, null);
                if (current == null || interval == item.getFromInterval()) {
                    if (current != null && i.hasPrevious())
                        i.previous();
                    i.add(item);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Cannot include item: " + item);
    }

    /**
     * @param timeIntervalList time interval list
     * @param items trains cycle items
     * @return if the time interval list is covered
     */
    public boolean isTimeIntervalListCovered(List<TimeInterval> timeIntervalList, List<TrainsCycleItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }
        if (items.isEmpty()) {
            return false;
        }
        Map<TimeInterval, Boolean> map = this.getTimeIntervalListMapCoverage(timeIntervalList, items);
        for (Boolean cover : map.values()) {
            if (Boolean.FALSE.equals(cover))
                return false;
        }
        return true;
    }

    /**
     * @param timeIntervalList time interval list
     * @param items trains cycle items
     * @param interval checked interval
     * @return if the time interval is covered by given items
     */
    public boolean isTimeIntervalCovered(List<TimeInterval> timeIntervalList, List<TrainsCycleItem> items, TimeInterval interval) {
        if (items == null)
            throw new IllegalArgumentException("List cannot be null");
        if (items.isEmpty())
            return false;
        Map<TimeInterval, Boolean> map = this.getTimeIntervalListMapCoverage(timeIntervalList, items);
        Boolean result = map.get(interval);
        if (result == null)
            throw new IllegalArgumentException("Time interval is not in list.");
        return result.booleanValue();
    }

    /**
     * returns list of uncovered time intervals.
     * @param timeIntervalList time interval list of the train
     * @param items trains cycle items
     * @return uncovered intervals
     */
    public List<Tuple<TimeInterval>> getAllUncoveredParts(List<TimeInterval> timeIntervalList, List<TrainsCycleItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }
        List<Tuple<TimeInterval>> result = new LinkedList<Tuple<TimeInterval>>();
        Map<TimeInterval, Boolean> map = this.createMapFromTimeIntervalList(timeIntervalList);
        this.addCoverageToMap(map, items);
        Tuple<TimeInterval> current = null;
        TimeInterval last = null;
        for (Map.Entry<TimeInterval, Boolean> entry : map.entrySet()) {
            if (entry.getValue() == Boolean.FALSE) {
                if (current == null)
                    current = new Tuple<TimeInterval>((last == null) ? entry.getKey() : last, null);
            }
            if (entry.getValue() == Boolean.TRUE) {
                if (current != null) {
                    current.second = entry.getKey();
                    result.add(current);
                    current = null;
                }
            }
            last = entry.getKey();
        }
        if (current != null) {
            current.second = last;
            result.add(current);
        }
        return result;
    }

    /**
     * returns list of uncovered time intervals.
     * @param timeIntervalList time interval list of the train
     * @param items trains cycle items
     * @return uncovered intervals
     */
    public List<List<TimeInterval>> getAllUncoveredLists(List<TimeInterval> timeIntervalList, List<TrainsCycleItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }
        List<List<TimeInterval>> result = new LinkedList<List<TimeInterval>>();
        Map<TimeInterval, Boolean> map = this.createMapFromTimeIntervalList(timeIntervalList);
        this.addCoverageToMap(map, items);
        List<TimeInterval> current = null;
        TimeInterval last = null;
        for (Map.Entry<TimeInterval, Boolean> entry : map.entrySet()) {
            if (entry.getValue() == Boolean.FALSE) {
                if (current == null) {
                    current = new LinkedList<TimeInterval>();
                    if (last != null)
                        current.add(last);
                }
                current.add(entry.getKey());
            }
            if (entry.getValue() == Boolean.TRUE) {
                if (current != null) {
                    current.add(entry.getKey());
                    result.add(current);
                    current = null;
                }
            }
            last = entry.getKey();
        }
        if (current != null) {
            current.add(last);
            result.add(current);
        }
        return result;
    }

    /**
     * returns list with time interval coverage.
     * @param timeIntervalList time interval list
     * @param items trains cycle items
     * @return coverage
     */
    public List<Pair<TimeInterval, Boolean>> getTimeIntervalListCoverage(List<TimeInterval> timeIntervalList, List<TrainsCycleItem> items) {
        List<Pair<TimeInterval, Boolean>> result = new LinkedList<Pair<TimeInterval, Boolean>>();
        Map<TimeInterval, Boolean> map = this.getTimeIntervalListMapCoverage(timeIntervalList, items);
        for (Map.Entry<TimeInterval, Boolean> entry : map.entrySet()) {
            result.add(new Pair<TimeInterval, Boolean>(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /**
     * returns ordered map with coverage of time intervals.
     * @param timeIntervalList time interval list
     * @param items trains cycle items
     * @return coverage
     */
    public Map<TimeInterval, Boolean> getTimeIntervalListMapCoverage(List<TimeInterval> timeIntervalList, List<TrainsCycleItem> items) {
        Map<TimeInterval, Boolean> map = this.createMapFromTimeIntervalList(timeIntervalList);
        this.addCoverageToMap(map, items);
        return map;
    }

    /**
     * tests if the trains cycle item can be safely added.
     * @param timeIntervalList time interval list
     * @param items list of current trains cycle items
     * @param newItem item to be added
     * @param ignoredItem item that should be ignored in the test
     * @param overlapping if the new item can be overlapped
     * @return result of the test
     */
    public boolean testAddCycle(List<TimeInterval> timeIntervalList, List<TrainsCycleItem> items, TrainsCycleItem newItem, TrainsCycleItem ignoredItem, boolean overlapping) {
        // check if the new item can be projected on interval
        if (!checkNodes(timeIntervalList, newItem)) {
            return false;
        }
        // if overlapping is allowed return true
        if (overlapping)
            return true;

        // test not overlapping cycle item
        Map<TimeInterval, Boolean> map = this.createMapFromTimeIntervalList(timeIntervalList);
        Set<TrainsCycleItem> itemsSet = new HashSet<TrainsCycleItem>(items);
        // remove ignored item
        if (ignoredItem != null)
            itemsSet.remove(ignoredItem);
        this.addCoverageToMap(map, itemsSet);

        boolean in = false;
        for (Map.Entry<TimeInterval, Boolean> entry : map.entrySet()) {
            if (entry.getKey() == newItem.getToInterval()) {
                break;
            }
            // do not test first and last interval of the trains cycle
            if (in && Boolean.TRUE.equals(entry.getValue()))
                return false;
            if (!in && (entry.getKey() == newItem.getFromInterval())) {
                in = true;
            }
        }

        return true;
    }

    /**
     * adds coverage to map of time intervals.
     * @param map map of time intervals
     * @param items collection of trains cycle items
     */
    private void addCoverageToMap(Map<TimeInterval, Boolean> map, Collection<TrainsCycleItem> items) {
        for (TrainsCycleItem item : items) {
            this.addCoverageToMap(map, item);
        }
    }

    /**
     * adds coverage to map of time intervals. It expects ordered map (LinkedHashMap).
     * @param map map of time intervals
     * @param item trains cycle item
     */
    private void addCoverageToMap(Map<TimeInterval, Boolean> map, TrainsCycleItem item) {
        boolean in = false;
        for (Map.Entry<TimeInterval, Boolean> entry : map.entrySet()) {
            if (!in && (entry.getKey() == item.getFromInterval())) {
                in = true;
            }
            if (in)
                entry.setValue(Boolean.TRUE);
            if (entry.getKey() == item.getToInterval()) {
                break;
            }
        }
    }

    /**
     * @param timeIntervalList time interval list
     * @return map with intervals (keys are in insert-order)
     */
    private Map<TimeInterval,Boolean> createMapFromTimeIntervalList(List<TimeInterval> timeIntervalList) {
        Map<TimeInterval, Boolean> map = new LinkedHashMap<TimeInterval, Boolean>();
        for (TimeInterval interval : timeIntervalList) {
            map.put(interval, Boolean.FALSE);
        }
        return map;
    }

    /**
     * @param i iterator of trains cycle items
     * @param ignored cycle item which should be ignored
     * @return next item or <code>null</code> in any other case
     */
    private TrainsCycleItem getNext(Iterator<TrainsCycleItem> i, TrainsCycleItem ignored) {
        while (i.hasNext()) {
            TrainsCycleItem item = i.next();
            if (item != ignored) {
                return item;
            }
        }
        return null;
    }

    /**
     * @param timeIntervalList time interval list
     * @param item trains cycle item
     * @return if the nodes of trains cycle item belong to time interval in correct order
     */
    private boolean checkNodes(List<TimeInterval> timeIntervalList, TrainsCycleItem item) {
        if (!item.getFromInterval().isNodeOwner() || !item.getToInterval().isNodeOwner())
            throw new IllegalArgumentException("TrainsCycleItem intervals doesn't belong to nodes.");
        if (item.getFromInterval() == item.getToInterval()) {
            return false;
        }
        boolean in = false;
        for (TimeInterval interval : timeIntervalList) {
            if (interval.isNodeOwner()) {
                if (!in && (interval == item.getFromInterval())) {
                    in = true;
                }
                if (interval == item.getToInterval()) {
                    return in;
                }
            }
        }
        return false;
    }
}
