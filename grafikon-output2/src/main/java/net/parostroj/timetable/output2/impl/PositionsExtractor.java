package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainsCycleSort;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.TransformUtil;

/**
 * Extracts list of positions from train diagram.
 *
 * @author jub
 */
public class PositionsExtractor {

    private TrainDiagram diagram;
    private AttributesExtractor ae = new AttributesExtractor();


    public PositionsExtractor(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public List<Position> getStartPositionsEngines() {
        List<Position> result = new LinkedList<Position>();
        for (TrainsCycle ecCycle : this.sortTrainsCycleList(diagram.getCycles(TrainsCycleType.ENGINE_CYCLE))) {
            if (!ecCycle.isEmpty()) {
                TrainsCycleItem start = ecCycle.iterator().next();
                String startName = start.getFromInterval().getOwnerAsNode().getName();
                String startTrack = start.getFromInterval().getTrack().getNumber();
                String startTime = diagram.getTimeConverter().convertIntToXml(start.getStartTime());
                result.add(new Position(ecCycle.getName(), TransformUtil.getEngineCycleDescription(ecCycle), startName, startTrack, startTime, start.getTrain().getName(), ae.extract(ecCycle.getAttributes())));
            }
        }
        return result;
    }

    public List<Position> getStartPositionsTrainUnits() {
        List<Position> result = new LinkedList<Position>();
        for (TrainsCycle tucCycle : this.sortTrainsCycleList(diagram.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE))) {
            if (!tucCycle.isEmpty()) {
                TrainsCycleItem start = tucCycle.iterator().next();
                String startName = start.getFromInterval().getOwnerAsNode().getName();
                String startTrack = start.getFromInterval().getTrack().getNumber();
                String startTime = diagram.getTimeConverter().convertIntToXml(start.getStartTime());
                result.add(new Position(tucCycle.getName(), tucCycle.getDescription(), startName, startTrack, startTime, start.getTrain().getName(), ae.extract(tucCycle.getAttributes())));
            }
        }
        return result;
    }

    public List<Position> getEndPositionsEngines() {
        List<Position> result = new LinkedList<Position>();
        for (TrainsCycle ecCycle : this.sortTrainsCycleList(diagram.getCycles(TrainsCycleType.ENGINE_CYCLE))) {
            if (!ecCycle.isEmpty()) {
                TrainsCycleItem end = ecCycle.getItems().get(ecCycle.getItems().size() - 1);
                String endName = end.getToInterval().getOwnerAsNode().getName();
                String endTrack = end.getToInterval().getTrack().getNumber();
                String endTime = diagram.getTimeConverter().convertIntToXml(end.getEndTime());
                result.add(new Position(ecCycle.getName(), TransformUtil.getEngineCycleDescription(ecCycle), endName, endTrack, endTime, end.getTrain().getName(), ae.extract(ecCycle.getAttributes())));
            }
        }
        return result;
    }

    public List<Position> getEndPositionsTrainUnits() {
        List<Position> result = new LinkedList<Position>();
        for (TrainsCycle tucCycle : this.sortTrainsCycleList(diagram.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE))) {
            if (!tucCycle.isEmpty()) {
                TrainsCycleItem end = tucCycle.getItems().get(tucCycle.getItems().size() - 1);
                String endName = end.getToInterval().getOwnerAsNode().getName();
                String endTrack = end.getToInterval().getTrack().getNumber();
                String endTime = diagram.getTimeConverter().convertIntToXml(end.getEndTime());
                result.add(new Position(tucCycle.getName(), tucCycle.getDescription(), endName, endTrack, endTime, end.getTrain().getName(), ae.extract(tucCycle.getAttributes())));
            }
        }
        return result;
    }

    private List<TrainsCycle> sortTrainsCycleList(List<TrainsCycle> list) {
        TrainsCycleSort sort = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        return sort.sort(list);
    }
}
