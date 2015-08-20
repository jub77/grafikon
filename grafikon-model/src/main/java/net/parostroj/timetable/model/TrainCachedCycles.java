package net.parostroj.timetable.model;

import java.util.*;

import com.google.common.collect.*;

import net.parostroj.timetable.utils.*;

/**
 * Cached map with time intervals and cycle items.
 *
 * @author jub
 */
class TrainCachedCycles {

    private final Map<TimeInterval, Multimap<TrainsCycleType, TrainsCycleItem>> map;

    public TrainCachedCycles() {
        map = new HashMap<TimeInterval, Multimap<TrainsCycleType, TrainsCycleItem>>();
    }

    public void add(List<TimeInterval> intervals, TrainsCycleItem item) {
        Iterable<TimeInterval> iterable = CollectionUtils.closedIntervalIterable(
                intervals, item.getFromInterval(), item.getToInterval());
        for (TimeInterval interval : iterable) {
            this.add(interval, item);
        }
    }

    private void add(TimeInterval interval, TrainsCycleItem item) {
        Multimap<TrainsCycleType, TrainsCycleItem> types = map.get(interval);
        if (types == null) {
            types = HashMultimap.create();
            map.put(interval, types);
        }
        types.put(item.getCycle().getType(), item);
    }

    public void remove(TrainsCycleItem item) {
        for (Multimap<TrainsCycleType, TrainsCycleItem> types : map.values()) {
            types.remove(item.getCycle().getType(), item);
        }
    }

    public Collection<TrainsCycleItem> get(TimeInterval interval, TrainsCycleType type) {
        Collection<TrainsCycleItem> items = null;
        Multimap<TrainsCycleType, TrainsCycleItem> types = map.get(interval);
        if (types != null) {
            items = types.get(type);
        } else {
            items = Collections.emptyList();
        }
        return items;
    }

    public boolean isCovered(TimeInterval interval, TrainsCycleType type) {
        Collection<TrainsCycleItem> list = this.get(interval, type);
        return !list.isEmpty();
    }

    public boolean isCovered(TimeInterval interval, TrainsCycleType type, TrainsCycleItem ignored) {
        if (ignored == null) {
            return isCovered(interval, type);
        } else {
            Collection<TrainsCycleItem> list = this.get(interval, type);
            boolean contains = list.contains(ignored);
            if (contains) {
                return list.size() > 1;
            } else {
                return !list.isEmpty();
            }
        }
    }

    public boolean isCovered(List<TimeInterval> intervals, TrainsCycleType type) {
        for (TimeInterval interval : intervals) {
            if (!this.isCovered(interval, type)) {
                return false;
            }
        }
        return true;
    }

    public List<Tuple<TimeInterval>> getUncovered(List<TimeInterval> intervals, TrainsCycleType type) {
        TimeInterval last = null;
        ResultList<Tuple<TimeInterval>> result = new ResultList<Tuple<TimeInterval>>();
        Tuple<TimeInterval> current = null;
        for (TimeInterval interval : intervals) {
            boolean covered = isCovered(interval, type);
            if (!covered) {
                if (current == null) {
                    current = new Tuple<TimeInterval>((last == null) ? interval : last, null);
                }
            } else {
                if (current != null) {
                    current.second = interval;
                    result.add(current);
                    current = null;
                }
            }
            last = interval;
        }
        if (current != null) {
            current.second = last;
            result.add(current);
        }
        return result.get();
    }

    public List<Pair<TimeInterval, Boolean>> getCoverage(List<TimeInterval> intervals, TrainsCycleType type) {
        List<Pair<TimeInterval, Boolean>> result = new LinkedList<Pair<TimeInterval,Boolean>>();
        for (TimeInterval interval : intervals) {
            result.add(new Pair<TimeInterval, Boolean>(interval, this.isCovered(interval, type)));
        }
        return result;
    }

    public boolean testAddCycle(List<TimeInterval> intervals, TrainsCycleItem newItem, TrainsCycleItem ignoredItem, boolean overlapping) {
        // check if the new item can be projected on interval
        if (!checkNodes(intervals, newItem)) {
            return false;
        }
        // if overlapping is allowed return true
        if (overlapping) {
            return true;
        }

        TrainsCycleType type = newItem.getCycle().getType();

        // test not overlapping cycle item
        boolean in = false;
        for (TimeInterval interval : intervals) {
            boolean covered = isCovered(interval, type, ignoredItem);
            if (interval == newItem.getToInterval()) {
                break;
            }
            // do not test first and last interval of the trains cycle
            if (in && covered) {
                return false;
            }
            if (!in && (interval == newItem.getFromInterval())) {
                in = true;
            }
        }

        return true;
    }

    /**
     * @param timeIntervalList time interval list
     * @param item trains cycle item
     * @return if the nodes of trains cycle item belong to time interval in correct order
     */
    private boolean checkNodes(List<TimeInterval> timeIntervalList, TrainsCycleItem item) {
        if (!item.getFromInterval().isNodeOwner() || !item.getToInterval().isNodeOwner()) {
            throw new IllegalArgumentException("TrainsCycleItem intervals doesn't belong to nodes.");
        }
        if (item.getFromInterval() == item.getToInterval()) {
            return false;
        }

        Iterator<TimeInterval> iterator = Iterators.filter(timeIntervalList.iterator(), interval -> interval.isNodeOwner());
        PeekingIterator<TimeInterval> peekingIterator = Iterators.peekingIterator(iterator);
        boolean found = CollectionUtils.advanceTo(peekingIterator, interval -> interval == item.getFromInterval());
        if (found) {
            found = CollectionUtils.advanceTo(peekingIterator, interval -> interval == item.getToInterval());
        }
        return found;
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
        if (!testAddCycle(timeIntervalList, item, null, overlapping))
            throw new IllegalArgumentException("Overlapping item.");
        ListIterator<TrainsCycleItem> i = items.listIterator();
        TrainsCycleItem current = i.hasNext() ? i.next() : null;
        for (TimeInterval interval : timeIntervalList) {
            if (interval.isNodeOwner()) {
                if (current != null && interval == current.getFromInterval()) {
                    current = i.hasNext() ? i.next() : null;
                }
                if (current == null || interval == item.getFromInterval()) {
                    if (current != null && i.hasPrevious()) {
                        i.previous();
                    }
                    i.add(item);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Cannot include item: " + item);
    }
}
