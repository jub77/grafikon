package net.parostroj.timetable.output2.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.actions.ElementSort;
import net.parostroj.timetable.actions.TrainsCycleComparator;
import net.parostroj.timetable.filters.ModelPredicates;
import net.parostroj.timetable.model.Interval;
import net.parostroj.timetable.model.IntervalFactory;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.utils.Pair;

/**
 * Extracts list of positions from train diagram. End positions does not support starting time.
 *
 * @author jub
 */
public class PositionsExtractor {

    private final TrainDiagram diagram;
    private final AttributesExtractor ae = new AttributesExtractor();

    public PositionsExtractor(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public List<Cycles> getStartPositionsCustom(Integer startingTime) {
        return getPositionsCustom(this::getStartPositions, startingTime);
    }

    /**
     * Does not support starting time.
     */
    public List<Cycles> getEndPositionsCustom(Integer startingTime) {
        return getPositionsCustom(this::getEndPositions, startingTime);
    }

    private List<Cycles> getPositionsCustom(BiFunction<Collection<TrainsCycle>, Integer, List<Position>> function, Integer startingTime) {
        List<Cycles> cyclesList = new ArrayList<Cycles>();
        for (TrainsCycleType type : diagram.getCycleTypes()) {
            if (!type.isDefaultType()) {
                Cycles cycles = new Cycles();
                cycles.setName(type.getLocalizedName());
                cycles.setPositions(function.apply(type.getCycles(), startingTime));
                cyclesList.add(cycles);
            }
        }
        return cyclesList;
    }

    public List<Position> getStartPositions(Collection<TrainsCycle> cycles, Integer startingTime) {
        List<Position> result = new LinkedList<Position>();
        for (Pair<TrainsCycleItem, TimeInterval> cycleItem : this.getItemStarts(cycles, startingTime)) {
            TrainsCycleItem start = cycleItem.first;
            TrainsCycle cycle = start.getCycle();
            TimeInterval interval = cycleItem.second;
            String startName = interval.getOwnerAsNode().getName();
            String startTrack = interval.getTrack().getNumber();
            String startTime = diagram.getTimeConverter().convertIntToXml(interval.getEnd());
            result.add(new Position(cycle.getName(), cycle.getDisplayDescription(), startName,
                    startTrack, startTime, start.getTrain().getName(),
                    ae.extract(cycle.getAttributes())));
        }
        return result;
    }

    /**
     * Does not support starting time.
     */
    public List<Position> getEndPositions(Collection<TrainsCycle> cycles, Integer startingTime) {
        List<Position> result = new LinkedList<Position>();
        for (TrainsCycle cycle : this.sortTrainsCycleList(cycles)) {
            if (!cycle.isEmpty()) {
                TrainsCycleItem end = cycle.getItems().get(cycle.getItems().size() - 1);
                String endName = end.getToInterval().getOwnerAsNode().getName();
                String endTrack = end.getToInterval().getTrack().getNumber();
                String endTime = diagram.getTimeConverter().convertIntToXml(end.getEndTime());
                result.add(new Position(cycle.getName(), cycle.getDisplayDescription(), endName,
                        endTrack, endTime, end.getTrain().getName(),
                        ae.extract(cycle.getAttributes())));
            }
        }
        return result;
    }

    private List<Pair<TrainsCycleItem, TimeInterval>> getItemStarts(Collection<TrainsCycle> cycles, Integer startingTime) {
        startingTime = startingTime == null ? 0 : startingTime;
        List<Pair<TrainsCycleItem, TimeInterval>> itemStarts = new ArrayList<>();
        for (TrainsCycle cycle : sortTrainsCycleList(cycles)) {
            TrainsCycleItem sItem = null;
            boolean added = false;
            for (TrainsCycleItem item : cycle) {
                Interval nInterval = IntervalFactory.createInterval(item.getStartTime(), item.getEndTime()).normalize();
                if (nInterval.isOverThreshold(startingTime)) {
                    // go through intervals ...
                    int lStartTime = startingTime + (startingTime < item.getStartTime() ? TimeInterval.DAY : 0);
                    for (TimeInterval interval : Iterables.filter(item.getIntervals(), ModelPredicates::nodeInterval)) {
                        if (interval.isStop() && !interval.isLast() && interval.getEnd() >= lStartTime) {
                            itemStarts.add(new Pair<>(item, interval));
                            added = true;
                            break;
                        }
                    }
                }
                if (sItem == null && nInterval.getStart() > startingTime) {
                    sItem = item;
                }
            }
            if (!added) {
                if (sItem == null) {
                    sItem = cycle.getFirstItem();
                }
                if (sItem != null) {
                    itemStarts.add(new Pair<>(sItem, sItem.getFromInterval()));
                }
            }
        }
        return itemStarts;
    }

    private List<TrainsCycle> sortTrainsCycleList(Collection<TrainsCycle> list) {
        ElementSort<TrainsCycle> sort = new ElementSort<>(new TrainsCycleComparator());
        return sort.sort(list);
    }
}
