package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.utils.*;

/**
 * Extracts information for station timetables.
 *
 * @author jub
 */
public class StationTimetablesExtractor {

    private final TrainDiagram diagram;
    private final List<Node> nodes;
    private final TimeConverter converter;
    private final boolean techTime;

    public StationTimetablesExtractor(TrainDiagram diagram, List<Node> nodes, boolean techTime) {
        this.diagram = diagram;
        this.nodes = nodes;
        this.techTime = techTime;
        this.converter = diagram.getTimeConverter();
    }

    public List<StationTimetable> getStationTimetables() {
        List<StationTimetable> result = new LinkedList<StationTimetable>();

        for (Node node : nodes) {
            StationTimetable timetable = new StationTimetable(node.getName());

            // process rows ...
            for (TimeInterval interval : this.collectIntervals(node)) {
                if (techTime || !interval.isTechnological()) {
                    timetable.getRows().add(this.createRow(interval));
                }
            }

            result.add(timetable);
        }

        return result;
    }

    /**
     * collects intervals for given station.
     *
     * @param node station
     * @return list of intervals
     */
    private List<TimeInterval> collectIntervals(Node node) {
        TimeIntervalList list = new TimeIntervalList();
        for (NodeTrack track : node.getTracks()) {
            for (TimeInterval i : track.getTimeIntervalList()) {
                list.addIntervalByNormalizedStartTime(i);
            }
        }
        return list;
    }

    private StationTimetableRow createRow(TimeInterval interval) {
        TimeInterval from = interval.getTrain().getIntervalBefore(interval);
        TimeInterval to = interval.getTrain().getIntervalAfter(interval);

        String fromNodeName = TransformUtil.getFromAbbr(interval);
        String toNodeName = TransformUtil.getToAbbr(interval);
        String endNodeName = (interval.isLast() || interval.isTechnological()) ? null : interval.getTrain().getEndNode().getAbbr();

        String fromTime = (from == null && !interval.isTechnological()) ? null : converter.convertIntToXml(interval.getStart());
        String toTime = (to == null && !interval.isTechnological()) ? null : converter.convertIntToXml(interval.getEnd());
        StationTimetableRow row = new StationTimetableRow(interval.getTrain().getName(), fromNodeName, fromTime, toNodeName, toTime, endNodeName, interval.getTrack().getNumber());
        this.addOtherData(interval, row);
        return row;
    }

    private void addOtherData(TimeInterval interval, StationTimetableRow row) {
        // technological time handle differently
        row.setTechnologicalTime(interval.isTechnological());
        if (row.isTechnologicalTime())
            return;

        row.setLength(this.getLength(interval));
        this.addEnginesAndTrainUnits(interval, TrainsCycleType.ENGINE_CYCLE, row.getEngine());
        this.addEnginesAndTrainUnits(interval, TrainsCycleType.TRAIN_UNIT_CYCLE, row.getTrainUnit());
        for (TrainsCycleType type : diagram.getCycleTypes()) {
            if (!TrainsCycleType.isDefaultType(type.getName())) {
                this.addCycles(interval, type, row.getCycle());
            }
        }
        row.setComment((String) interval.getAttribute(TimeInterval.ATTR_COMMENT));
        row.setOccupied(Boolean.TRUE.equals(interval.getAttribute(TimeInterval.ATTR_OCCUPIED)));
    }

    private void addCycles(TimeInterval interval, TrainsCycleType type, List<CycleWithTypeFromTo> cycles) {
        Train train = interval.getTrain();
        for (TrainsCycleItem item : train.getCycles(type.getName())) {
            if (item.getToInterval() == interval) {
                // end
                TrainsCycleItem itemNext = item.getCycle().getNextItem(item);
                TrainsCycle cycle = item.getCycle();
                cycles.add(new CycleWithTypeFromTo(false, false, cycle.getName(),
                        cycle.getDescription(),
                        itemNext != null ? itemNext.getTrain().getName() : null,
                        itemNext != null ? converter.convertIntToXml(itemNext.getStartTime()) : null,
                        type.getName()));
            }
            if (item.getFromInterval() == interval) {
                // start
                TrainsCycleItem itemPrev = item.getCycle().getPreviousItem(item);
                TrainsCycle cycle = item.getCycle();
                cycles.add(new CycleWithTypeFromTo(itemPrev == null, true, cycle.getName(),
                        cycle.getDescription(),
                        itemPrev != null ? itemPrev.getTrain().getName() : null,
                        itemPrev != null ? converter.convertIntToXml(itemPrev.getEndTime()) : null,
                        type.getName()));
            }
        }
    }

    private void addEnginesAndTrainUnits(TimeInterval interval, String type, List<CycleFromTo> cycles) {
        Train train = interval.getTrain();
        for (TrainsCycleItem item : train.getCycles(type)) {
            if (item.getToInterval() == interval) {
                // end
                TrainsCycleItem itemNext = item.getCycle().getNextItem(item);
                TrainsCycle cycle = item.getCycle();
                cycles.add(new CycleFromTo(false, false, cycle.getName(),
                        type.equals(TrainsCycleType.ENGINE_CYCLE) ?  TransformUtil.getEngineCycleDescription(item.getCycle()) : cycle.getDescription(),
                        itemNext != null ? itemNext.getTrain().getName() : null,
                        itemNext != null ? converter.convertIntToXml(itemNext.getStartTime()) : null));
            }
            if (item.getFromInterval() == interval) {
                // start
                TrainsCycleItem itemPrev = item.getCycle().getPreviousItem(item);
                TrainsCycle cycle = item.getCycle();
                cycles.add(new CycleFromTo(itemPrev == null, true, cycle.getName(),
                        type.equals(TrainsCycleType.ENGINE_CYCLE) ?  TransformUtil.getEngineCycleDescription(item.getCycle()) : cycle.getDescription(),
                        itemPrev != null ? itemPrev.getTrain().getName() : null,
                        itemPrev != null ? converter.convertIntToXml(itemPrev.getEndTime()) : null));
            }
        }
    }

    private LengthInfo getLength(TimeInterval interval) {
        LengthInfo lengthInfo = null;
        Train train = interval.getTrain();
        if (train.getIntervalAfter(interval) != null && interval.isStop() && train.getType().getAttributes().getBool(TrainType.ATTR_SHOW_WEIGHT_INFO)) {
            Pair<Node, Integer> length = TrainsHelper.getNextLength(interval.getOwnerAsNode(), train, TrainsHelper.NextType.LAST_STATION);
            // if length was calculated
            if (length != null && length.second != null) {
                // update length with station lengths
                lengthInfo = new LengthInfo();
                lengthInfo.setLength(length.second);
                LengthUnit lengthUnitObj = (LengthUnit) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT);
                lengthInfo.setLengthInAxles(lengthUnitObj != null && lengthUnitObj == LengthUnit.AXLE);
                lengthInfo.setLengthUnit(lengthUnitObj != null ? lengthUnitObj.getUnitsOfString() : null);
                lengthInfo.setStationAbbr(length.first.getAbbr());
            }
        }
        return lengthInfo;
    }
}
