package net.parostroj.timetable.output2.impl;

import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.*;

/**
 * Extracts information for station timetables.
 *
 * @author jub
 */
public class StationTimetablesExtractor {

    private TrainDiagram diagram;
    private List<Node> nodes;

    public StationTimetablesExtractor(TrainDiagram diagram, List<Node> nodes) {
        this.diagram = diagram;
        this.nodes = nodes;
    }

    public List<StationTimetable> getStationTimetables() {
        List<StationTimetable> result = new LinkedList<StationTimetable>();

        for (Node node : nodes) {
            StationTimetable timetable = new StationTimetable(node.getName());

            // process rows ...
            for (TimeInterval interval : this.collectIntervals(node)) {
                timetable.getRows().add(this.createRow(interval));
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

        String fromTime = (from == null && !interval.isTechnological()) ? null : TimeConverter.convertFromIntToText(interval.getStart());
        String toTime = (to == null && !interval.isTechnological()) ? null : TimeConverter.convertFromIntToText(interval.getEnd());
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
        this.addEngines(interval, row);
        this.addTrainUnits(interval, row);
        row.setComment((String)interval.getAttribute("comment"));
        row.setOccupied(Boolean.TRUE.equals(interval.getAttribute("occupied")));
    }

    private void addEngines(TimeInterval interval, StationTimetableRow row) {
        Train train = interval.getTrain();
        for (TrainsCycleItem item : train.getCycles(TrainsCycleType.ENGINE_CYCLE)) {
            if (item.getToInterval() == interval) {
                // end
                TrainsCycleItem itemNext = item.getCycle().getNextItem(item);
                if (itemNext != null) {
                    row.getEngineTo().add(new EngineTo(item.getCycle().getName(), itemNext.getTrain().getName(), TimeConverter.convertFromIntToText(itemNext.getStartTime())));
                }
            }
            if (item.getFromInterval() == interval) {
                // start
                row.getEngineFrom().add(new EngineFrom(item.getCycle().getName(), TransformUtil.getEngineCycleDescription(item.getCycle())));
            }
        }
    }

    private void addTrainUnits(TimeInterval interval, StationTimetableRow row) {
        Train train = interval.getTrain();
        for (TrainsCycleItem item : train.getCycles(TrainsCycleType.TRAIN_UNIT_CYCLE)) {
            // end
            if (item.getToInterval() == interval) {
                TrainsCycleItem itemNext = item.getCycle().getNextItem(item);
                if (itemNext != null) {
                    row.getTrainUnitTo().add(new TrainUnitTo(item.getCycle().getName(), item.getCycle().getDescription(), itemNext.getTrain().getName(), TimeConverter.convertFromIntToText(itemNext.getStartTime())));
                }
            }
            // start
            if (item.getFromInterval() == interval) {
                row.getTrainUnitFrom().add(new TrainUnitFrom(item.getCycle().getName(), item.getCycle().getDescription()));
            }
        }
    }

    private LengthInfo getLength(TimeInterval interval) {
        LengthInfo lengthInfo = null;
        Train train = interval.getTrain();
        if (train.getIntervalAfter(interval) != null && interval.isStop() && train.getType().getCategory().getKey().equals("freight")) {
            Pair<Node, Integer> length = TrainsHelper.getNextLength(interval.getOwnerAsNode(), train, diagram);
            if (length == null) {
                // check old style comment
                Integer weight = TrainsHelper.getWeightFromAttribute(train);
                if (weight != null)
                    length = new Pair<Node, Integer>(train.getEndNode(), TrainsHelper.convertWeightToLength(train, diagram, weight));
            }
            // if length was calculated
            if (length != null && length.second != null) {
                // update length with station lengths
                length.second = TrainsHelper.updateNextLengthWithStationLengths(interval.getOwnerAsNode(), train, length.second);
                lengthInfo = new LengthInfo();
                lengthInfo.setLength(length.second);
                lengthInfo.setLengthInAxles(Boolean.TRUE.equals(diagram.getAttribute("station.length.in.axles")));
                lengthInfo.setLengthUnit((String)diagram.getAttribute("station.length.unit"));
                lengthInfo.setStationAbbr(length.first.getAbbr());
            }
        }
        return lengthInfo;
    }
}
