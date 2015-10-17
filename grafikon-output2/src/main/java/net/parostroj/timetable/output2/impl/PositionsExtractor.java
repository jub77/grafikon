package net.parostroj.timetable.output2.impl;

import java.util.*;
import java.util.function.BiFunction;

import net.parostroj.timetable.actions.ElementSort;
import net.parostroj.timetable.actions.TrainsCycleComparator;
import net.parostroj.timetable.model.*;
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
                cycles.setName(type.getName());
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
        List<Pair<TrainsCycleItem, TimeInterval>> itemStarts = new ArrayList<>();
        for (TrainsCycle cycle : sortTrainsCycleList(cycles)) {
            List<TrainsCycleItem> items = cycle.getItems();
            Pair<TrainsCycleItem, TimeInterval> itemStart = null;
            if (items.size() > 0) {
                itemStart = new Pair<TrainsCycleItem, TimeInterval>(items.get(0),
                        items.get(0).getFromInterval());
            }
            if (startingTime != null) {
                boolean stop = false;
                for (TrainsCycleItem item : items) {
                    TimeInterval start = item.getFromInterval();
                    TimeInterval end = item.getToInterval();
                    while (start != end && !stop) {
                        if (start.isNodeOwner()) {
                            if (startingTime < start.getEnd()) {
                                itemStart = new Pair<TrainsCycleItem, TimeInterval>(item, start);
                                stop = true;
                            }
                        }
                        start = start.getNextTrainInterval();
                    }
                    if (stop) {
                        break;
                    }
                }
            }
            if (itemStart != null) {
                itemStarts.add(itemStart);
            }
        }
        return itemStarts;
    }

    private List<TrainsCycle> sortTrainsCycleList(Collection<TrainsCycle> list) {
        ElementSort<TrainsCycle> sort = new ElementSort<>(new TrainsCycleComparator());
        return sort.sort(list);
    }
}
