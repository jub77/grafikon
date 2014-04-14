package net.parostroj.timetable.output2.impl;

import java.util.*;

import net.parostroj.timetable.actions.FreightHelper;
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
    private final Locale locale;

    public StationTimetablesExtractor(TrainDiagram diagram, List<Node> nodes, boolean techTime, Locale locale) {
        this.diagram = diagram;
        this.nodes = nodes;
        this.techTime = techTime;
        this.locale = locale;
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
        if (FreightHelper.isFreight(interval)) {
            List<FreightDst> freightDests = FreightHelper.convertFreightDst(interval, diagram.getFreightNet().getFreightToNodes(interval));
            if (!freightDests.isEmpty()) {
                ArrayList<String> fl = new ArrayList<String>(freightDests.size());
                for (FreightDst dst : freightDests) {
                    fl.add(dst.toString(locale));
                }
                row.setFreightTo(fl);
            }
            List<FNConnection> toTrains = diagram.getFreightNet().getTrainsFrom(interval);
            if (!toTrains.isEmpty()) {
                ArrayList<String> nt = new ArrayList<String>(toTrains.size());
                for (FNConnection conn : toTrains) {
                    nt.add(conn.getTo().getTrain().getName());
                }
                row.setFreightToTrain(nt);
            }
            List<FNConnection> trainsFrom = diagram.getFreightNet().getTrainsTo(interval);
            if (!trainsFrom.isEmpty()) {
                ArrayList<String> nt = new ArrayList<String>(trainsFrom.size());
                for (FNConnection conn : trainsFrom) {
                    nt.add(conn.getFrom().getTrain().getName());
                }
                row.setFreightFromTrain(nt);
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
        TrainType trainType = train.getType();
        if (train.getIntervalAfter(interval) != null && interval.isStop() && trainType != null && trainType.getAttributes().getBool(TrainType.ATTR_SHOW_WEIGHT_INFO)) {
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
