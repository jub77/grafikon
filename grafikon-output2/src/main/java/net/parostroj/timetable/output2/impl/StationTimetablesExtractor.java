package net.parostroj.timetable.output2.impl;

import static net.parostroj.timetable.actions.TrainsHelper.getNextLength;
import static net.parostroj.timetable.actions.TrainsHelper.isHelperEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import net.parostroj.timetable.actions.TrainsHelper.NextType;
import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TimeIntervalList;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.TranslatedString;
import net.parostroj.timetable.model.freight.FreightConnection;
import net.parostroj.timetable.model.freight.FreightConnectionPath;
import net.parostroj.timetable.model.freight.FreightConnectionStrategy;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.output2.util.OutputFreightUtil;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.TransformUtil;

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
    private final boolean adjacentSessions;
    private final Locale locale;
    private final FreightConnectionStrategy strategy;
    private final OutputFreightUtil outputFreightUtil;

    public StationTimetablesExtractor(TrainDiagram diagram, List<Node> nodes, boolean techTime,
            boolean adjacentSessions, Locale locale) {
        this.diagram = diagram;
        this.nodes = nodes;
        this.techTime = techTime;
        this.adjacentSessions = adjacentSessions;
        this.locale = locale;
        this.converter = diagram.getTimeConverter();
        this.strategy = diagram.getFreightNet().getConnectionStrategy();
        this.outputFreightUtil = new OutputFreightUtil();
    }

    public List<StationTimetable> getStationTimetables() {
        List<StationTimetable> result = new LinkedList<>();

        for (Node node : nodes) {
            StationTimetable timetable = new StationTimetable(node.getName());
            timetable.setType(node.getType());

            // regions + company
            Set<Region> regions = node.getRegions();
            if (!regions.isEmpty()) {
                timetable.setRegions(regions.stream()
                        .map(RegionInfo::convert)
                        .collect(Collectors.toList()));
            }
            Company company = node.getAttribute(Node.ATTR_COMPANY, Company.class);
            if (company != null) {
                timetable.setCompany(CompanyInfo.convert(company));
            }

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
        node.forEach(list::addIntervalByNormalizedStartTime);
        return list;
    }

    private StationTimetableRow createRow(TimeInterval interval) {
        TimeInterval from = interval.getTrain().getInterval(interval, -1);
        TimeInterval to = interval.getTrain().getInterval(interval, 1);

        String fromNodeName = TransformUtil.getFromAbbr(interval);
        String toNodeName = TransformUtil.getToAbbr(interval);
        String endNodeName = (interval.isLast() || interval.isTechnological()) ? null
                : interval.getTrain().getEndNode().getAbbr();

        String fromTime = (from == null && !interval.isTechnological()) ? null
                : converter.convertIntToXml(interval.getStart());
        String toTime = (to == null && !interval.isTechnological()) ? null
                : converter.convertIntToXml(interval.getEnd());
        StationTimetableRow row = new StationTimetableRow(interval.getTrain().getName(), fromNodeName, fromTime,
                toNodeName, toTime, endNodeName, interval.getTrack().getNumber());
        this.addFromAndToTrack(row, from, to);
        row.setStop(interval.getLength());
        this.addOtherData(interval, row);
        row.setRef(interval);
        return row;
    }

    private void addFromAndToTrack(StationTimetableRow row, TimeInterval from, TimeInterval to) {
        if (from != null && from.getOwner().getTracks().size() > 1) {
            row.setFromTrack(from.getTrack().getNumber());
        }
        if (to != null && to.getOwner().getTracks().size() > 1) {
            row.setToTrack(to.getTrack().getNumber());
        }
    }

    private void addOtherData(TimeInterval interval, StationTimetableRow row) {
        // technological time handle differently
        row.setTechnologicalTime(interval.isTechnological());
        if (row.isTechnologicalTime()) {
            return;
        }

        row.setLength(this.getLength(interval));

        BiFunction<TrainsCycle, TrainsCycleItem, TrainsCycleItem> nextF = getNextFunction();
        BiFunction<TrainsCycle, TrainsCycleItem, TrainsCycleItem> previousF = getPreviousFunction();

        this.addEnginesAndTrainUnits(interval, diagram.getEngineCycleType(), row.getEngine(),
                nextF, previousF);
        this.addEnginesAndTrainUnits(interval, diagram.getTrainUnitCycleType(), row.getTrainUnit(),
                nextF, previousF);
        for (TrainsCycleType type : diagram.getCycleTypes()) {
            if (!type.isDefaultType()) {
                this.addCycles(interval, type, row.getCycle(), nextF, previousF);
            }
        }
        if (interval.isFreight()) {
            List<? extends FreightConnection> freightDests = strategy.getFreightToNodes(interval);
            if (!freightDests.isEmpty()) {
                ArrayList<FreightDestinationInfo> fl = new ArrayList<>(freightDests.size());
                for (FreightConnection dst : outputFreightUtil.reorderFreightListByDirection(freightDests)) {
                    fl.add(FreightDestinationInfo.convert(locale, dst));
                }
                row.setFreightTo(fl);
            }
        }
        if (interval.isFreightConnection()) {
            Map<Train, List<FreightConnectionPath>> passedCargoDst = strategy.getFreightPassedInNode(interval);
            if (!passedCargoDst.isEmpty()) {
                List<FreightToTrain> fttl = new ArrayList<>();
                for (Map.Entry<Train, List<FreightConnectionPath>> entry : passedCargoDst.entrySet()) {
                    FreightToTrain ftt = new FreightToTrain();
                    ftt.setTrain(entry.getKey().getName());
                    List<? extends FreightConnection> mList = entry.getValue();
                    List<FreightDestinationInfo> fl = new ArrayList<>(mList.size());
                    for (FreightConnection dst : outputFreightUtil.reorderFreightListByDirection(mList)) {
                        fl.add(FreightDestinationInfo.convert(locale, dst));
                    }
                    ftt.setFreightTo(fl);
                    fttl.add(ftt);
                }
                row.setFreightToTrain(fttl);
            }
        }
        List<FNConnection> trainsFrom = diagram.getFreightNet().getTrainsTo(interval);
        if (!trainsFrom.isEmpty()) {
            ArrayList<TranslatedString> nt = new ArrayList<>(trainsFrom.size());
            for (FNConnection conn : trainsFrom) {
                nt.add(conn.getFrom().getTrain().getName());
            }
            row.setFreightFromTrain(nt);
        }
        row.setComment(interval.getComment());
        row.setOccupied(interval.getAttributes().getBool(TimeInterval.ATTR_OCCUPIED));
    }

    private void addCycles(TimeInterval interval, TrainsCycleType type, List<CycleWithTypeFromTo> cycles,
            BiFunction<TrainsCycle, TrainsCycleItem, TrainsCycleItem> nextItemF,
            BiFunction<TrainsCycle, TrainsCycleItem, TrainsCycleItem> previousItemF) {
        Train train = interval.getTrain();
        for (TrainsCycleItem item : train.getCycles(type)) {
            if (item.getToInterval() == interval) {
                // end
                TrainsCycle cycle = item.getCycle();
                TrainsCycleItem itemNext = nextItemF.apply(cycle, item);
                CycleWithTypeFromTo cycleFromTo = new CycleWithTypeFromTo(false, false, cycle.getName(),
                        cycle.getDescription(),
                        itemNext != null ? itemNext.getTrain().getName() : null,
                        itemNext != null ? converter.convertIntToXml(itemNext.getStartTime()) : null);
                cycleFromTo.setTypeKey(type.getKey());
                cycleFromTo.setTypeName(type.getName());
                this.updateAdjacent(cycleFromTo, item, itemNext);
                cycles.add(cycleFromTo);
            }
            if (item.getFromInterval() == interval) {
                // start
                TrainsCycle cycle = item.getCycle();
                TrainsCycleItem itemPrev = previousItemF.apply(cycle, item);
                CycleWithTypeFromTo cycleFromTo = new CycleWithTypeFromTo(itemPrev == null, true, cycle.getName(),
                        cycle.getDescription(),
                        itemPrev != null ? itemPrev.getTrain().getName() : null,
                        itemPrev != null ? converter.convertIntToXml(itemPrev.getEndTime()) : null);
                cycleFromTo.setTypeKey(type.getKey());
                cycleFromTo.setTypeName(type.getName());
                this.updateAdjacent(cycleFromTo, item, itemPrev);
                cycles.add(cycleFromTo);
            }
        }
    }

    private void addEnginesAndTrainUnits(TimeInterval interval, TrainsCycleType type, List<CycleFromTo> cycles,
            BiFunction<TrainsCycle, TrainsCycleItem, TrainsCycleItem> nextItemF,
            BiFunction<TrainsCycle, TrainsCycleItem, TrainsCycleItem> previousItemF) {
        Train train = interval.getTrain();
        for (TrainsCycleItem item : train.getCycles(type)) {
            CycleFromTo cycleFromTo = null;
            if (item.getToInterval() == interval) {
                // end
                TrainsCycle cycle = item.getCycle();
                TrainsCycleItem itemNext = nextItemF.apply(cycle, item);
                cycleFromTo = new CycleFromTo(false, false, cycle.getName(),
                        cycle.getDisplayDescription(),
                        itemNext != null ? itemNext.getTrain().getName() : null,
                        itemNext != null ? converter.convertIntToXml(itemNext.getStartTime()) : null);
                this.updateAdjacent(cycleFromTo, item, itemNext);
            }
            if (item.getFromInterval() == interval) {
                // start
                TrainsCycle cycle = item.getCycle();
                TrainsCycleItem itemPrev = previousItemF.apply(cycle, item);
                cycleFromTo = new CycleFromTo(itemPrev == null, true, cycle.getName(),
                        cycle.getDisplayDescription(),
                        itemPrev != null ? itemPrev.getTrain().getName() : null,
                        itemPrev != null ? converter.convertIntToXml(itemPrev.getEndTime()) : null);
                this.updateAdjacent(cycleFromTo, item, itemPrev);
            }
            if (cycleFromTo != null) {
                if (TrainsCycleType.isEngineType(type) && isHelperEngine(item)) {
                    cycleFromTo.setHelper(true);
                }
                cycles.add(cycleFromTo);
            }
        }
    }

    private LengthInfo getLength(TimeInterval interval) {
        LengthInfo lengthInfo = null;
        Train train = interval.getTrain();
        TrainType trainType = train.getType();
        if (!interval.isLast() && interval.isStop() && trainType != null
                && trainType.getAttributes().getBool(TrainType.ATTR_SHOW_WEIGHT_INFO)) {
            Pair<Node, Integer> length = getNextLength(interval.getOwnerAsNode(), train, NextType.LAST_STATION);
            // if length was calculated
            if (length != null && length.second != null) {
                // update length with station lengths
                lengthInfo = new LengthInfo();
                lengthInfo.setLength(length.second);
                LengthUnit lengthUnitObj = diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT, LengthUnit.class);
                lengthInfo.setLengthInAxles(LengthUnit.AXLE == lengthUnitObj);
                lengthInfo.setLengthUnit(lengthUnitObj);
                lengthInfo.setStationAbbr(length.first.getAbbr());
            }
        }
        return lengthInfo;
    }

    private BiFunction<TrainsCycle, TrainsCycleItem, TrainsCycleItem> getNextFunction() {
        return adjacentSessions ?
                (cycle, item) -> cycle.getNextItemCyclic(item) :
                (cycle, item) -> cycle.getNextItem(item);
    }

    private BiFunction<TrainsCycle, TrainsCycleItem, TrainsCycleItem> getPreviousFunction() {
        return adjacentSessions ?
                (cycle, item) -> cycle.getPreviousItemCyclic(item) :
                (cycle, item) -> cycle.getPreviousItem(item);
    }

    private void updateAdjacent(CycleFromTo cycle, TrainsCycleItem current, TrainsCycleItem adjacent) {
        if (adjacent != null && current.getCycle() != adjacent.getCycle()) {
            cycle.setAdjacent(adjacent.getCycle().getName());
        }
    }
}
