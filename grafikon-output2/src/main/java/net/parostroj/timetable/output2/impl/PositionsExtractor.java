package net.parostroj.timetable.output2.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.model.*;

/**
 * Extracts list of positions from train diagram.
 *
 * @author jub
 */
public class PositionsExtractor {

    private final TrainDiagram diagram;
    private final AttributesExtractor ae = new AttributesExtractor();

    public PositionsExtractor(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public List<Cycles> getStartPositionsCustom() {
        return getPositionsCustom(this::getStartPositions);
    }

    public List<Cycles> getEndPositionsCustom() {
        return getPositionsCustom(this::getEndPositions);
    }

    private List<Cycles> getPositionsCustom(Function<List<TrainsCycle>, List<Position>> function) {
        List<Cycles> cyclesList = new ArrayList<Cycles>();
        for (TrainsCycleType type : diagram.getCycleTypes()) {
            if (!type.isDefaultType()) {
                Cycles cycles = new Cycles();
                cycles.setName(type.getName());
                cycles.setPositions(function.apply(diagram.getCycles(type)));
                cyclesList.add(cycles);
            }
        }
        return cyclesList;
    }

    public List<Position> getStartPositions(List<TrainsCycle> cycles) {
        List<Position> result = new LinkedList<Position>();
        for (TrainsCycle cycle : this.sortTrainsCycleList(cycles)) {
            if (!cycle.isEmpty()) {
                TrainsCycleItem start = cycle.iterator().next();
                String startName = start.getFromInterval().getOwnerAsNode().getName();
                String startTrack = start.getFromInterval().getTrack().getNumber();
                String startTime = diagram.getTimeConverter().convertIntToXml(start.getStartTime());
                result.add(new Position(cycle.getName(), cycle.getDisplayDescription(), startName, startTrack, startTime, start.getTrain().getName(), ae.extract(cycle.getAttributes())));
            }
        }
        return result;
    }

    public List<Position> getEndPositions(List<TrainsCycle> cycles) {
        List<Position> result = new LinkedList<Position>();
        for (TrainsCycle cycle : this.sortTrainsCycleList(cycles)) {
            if (!cycle.isEmpty()) {
                TrainsCycleItem end = cycle.getItems().get(cycle.getItems().size() - 1);
                String endName = end.getToInterval().getOwnerAsNode().getName();
                String endTrack = end.getToInterval().getTrack().getNumber();
                String endTime = diagram.getTimeConverter().convertIntToXml(end.getEndTime());
                result.add(new Position(cycle.getName(), cycle.getDisplayDescription(), endName, endTrack, endTime, end.getTrain().getName(), ae.extract(cycle.getAttributes())));
            }
        }
        return result;
    }

    private List<TrainsCycle> sortTrainsCycleList(List<TrainsCycle> list) {
        TrainsCycleSort sort = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        return sort.sort(list);
    }
}
