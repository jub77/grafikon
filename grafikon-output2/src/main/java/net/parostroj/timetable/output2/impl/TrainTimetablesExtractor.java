package net.parostroj.timetable.output2.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.parostroj.timetable.actions.ElementSort;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineClass;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Node.Side;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.RouteSegment;
import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TranslatedString;
import net.parostroj.timetable.model.freight.FreightConnection;
import net.parostroj.timetable.model.freight.FreightConnectionStrategy;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.utils.Pair;

/**
 * Extracts information for train timetables.
 *
 * @author jub
 */
public class TrainTimetablesExtractor {

    private final TrainDiagram diagram;
    private final Collection<Train> trains;
    private final Collection<Route> routes;
    private final TrainsCycle cycle;
    private final Map<Pair<Line, Node>, Double> cachedRoutePositions;
    private final Locale locale;
    private final FreightConnectionStrategy strategy;

    public TrainTimetablesExtractor(TrainDiagram diagram, Collection<Train> trains, Collection<Route> routes, TrainsCycle cycle, Locale locale) {
        this.diagram = diagram;
        this.trains = ImmutableSet.copyOf(trains);
        this.routes = routes;
        this.cycle = cycle;
        this.locale = locale;
        this.cachedRoutePositions = new HashMap<>();
        this.strategy = diagram.getFreightNet().getConnectionStrategy();
    }

    public TrainTimetables getTrainTimetables() {
        List<TrainTimetable> result = new LinkedList<>();
        List<Text> texts = null;

        // trains
        for (Train train : trains) {
            result.add(this.createTimetable(train));
        }

        // texts
        for (TextItem item : diagram.getTextItems()) {
            Text text = this.createText(item);
            if (text != null) {
                if (texts == null) {
                    texts = new LinkedList<>();
                }
                texts.add(text);
            }
        }

        TrainTimetables timetables = new TrainTimetables(result);
        timetables.setTexts(texts);

        // routes
        timetables.setRoutes(RoutesExtractor.convert(routes, diagram));

        // route length unit
        LengthUnit unit = diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT, LengthUnit.class);
        if (unit != null) {
            timetables.setRouteLengthUnit(unit);
        }

        // route info (lower priority)
        timetables.setRouteNumbers(diagram.getAttribute(TrainDiagram.ATTR_ROUTE_NUMBERS, String.class));
        timetables.setRouteStations(diagram.getAttribute(TrainDiagram.ATTR_ROUTE_NODES, String.class));
        // validity
        timetables.setValidity(diagram.getAttribute(TrainDiagram.ATTR_ROUTE_VALIDITY, String.class));

        // cycle
        if (cycle != null) {
            DriverCyclesExtractor ex = new DriverCyclesExtractor(diagram, null, false);
            timetables.setCycle(ex.createCycle(cycle));
        }

        return timetables;
    }

    private TrainTimetable createTimetable(Train train) {
        TrainTimetable timetable = new TrainTimetable();
        timetable.setRef(train);
        timetable.setName(train.getName());
        timetable.setCompleteName(train.getCompleteName());
        this.extractRouteInfo(train, timetable);
        this.extractDieselElectric(train, timetable);
        if (train.getType() != null && train.getType().getCategory() != null) {
            timetable.setCategoryKey(train.getType().getCategory().getKey());
        }
        if (train.getAnalysis().oneLineHasAttribute(Line.ATTR_CONTROLLED, Boolean.TRUE))
            timetable.setControlled(true);
        WeightDataExtractor wex = new WeightDataExtractor(train, diagram.getTrainUnitCycleType());
        timetable.setWeightData(wex.getData());
        this.extractLengthData(train, timetable);
        this.extractRows(train, timetable);
        return timetable;
    }

    private void extractDieselElectric(Train train, TrainTimetable timetable) {
        if (train.getAttributes().getBool(Train.ATTR_DIESEL)) {
            timetable.setDiesel(true);
        }
        if (train.getAttributes().getBool(Train.ATTR_ELECTRIC)) {
            timetable.setElectric(true);
        }
    }

    private void extractRouteInfo(Train train, TrainTimetable timetable) {
        TextTemplate routeTemplate = train.getAttribute(Train.ATTR_ROUTE, TextTemplate.class);
        if (routeTemplate == null) {
            return;
        }
        String result = routeTemplate.evaluate(TextTemplate.getBinding(train));
        timetable.setRouteInfo(new LinkedList<RouteInfoPart>());
        // split
        String[] splitted = result.split("-");
        for (String sPart : splitted) {
            sPart = sPart.trim();
            timetable.getRouteInfo().add(new RouteInfoPart(sPart, null));
        }
    }

    private void extractLengthData(Train train, TrainTimetable timetable) {
        if (train.getAttributes().getBool(Train.ATTR_SHOW_STATION_LENGTH)) {
            // compute maximal length
            Pair<Node, Integer> length = TrainsHelper.getNextLength(train.getStartNode(), train, TrainsHelper.NextType.LAST_STATION);
            if (length != null && length.second != null) {
                // get length unit
                LengthUnit lengthUnitObj = diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT, LengthUnit.class);
                LengthData data = new LengthData();
                data.setLength(length.second);
                data.setLengthInAxles(LengthUnit.AXLE == lengthUnitObj);
                data.setLengthUnit(lengthUnitObj);
                timetable.setLengthData(data);
            }
        }
    }

    private void extractRows(Train train, TrainTimetable timetable) {
        timetable.setRows(new LinkedList<TrainTimetableRow>());
        Iterator<TimeInterval> i = train.getTimeIntervalList().iterator();
        TimeInterval lastLineI = null;
        while (i.hasNext()) {
            TimeInterval nodeI = i.next();
            TimeInterval lineI = i.hasNext() ? i.next() : null;

            if (nodeI.getOwnerAsNode().getType() == NodeType.SIGNAL) {
                lastLineI = lineI;
                continue;
            }

            TrainTimetableRow row = new TrainTimetableRow();
            row.setRef(nodeI);

            if (nodeI.getOwnerAsNode().getAttributes().getBool(Node.ATTR_CONTROL_STATION)) {
                row.setControlStation(true);
            }
            if (Node.IP_NEW_SIGNALS.equals(nodeI.getOwnerAsNode().getAttribute(Node.ATTR_INTERLOCKING_PLANT, String.class))) {
                row.setLightSignals(true);
            }
            row.setStation(nodeI.getOwnerAsNode().getName());
            row.setStationAbbr(nodeI.getOwnerAsNode().getAbbr());
            row.setStationType(nodeI.getOwnerAsNode().getType().getKey());
            if (!nodeI.isFirst())
                row.setArrival(diagram.getTimeConverter().convertIntToXml(nodeI.getStart()));
            if (!nodeI.isLast())
                row.setDeparture(diagram.getTimeConverter().convertIntToXml(nodeI.getEnd()));
            if (lineI != null) {
                row.setSpeed(lineI.getSpeed());
                row.setLineTracks(lineI.getOwnerAsLine().getTracks().size());
                row.setSetSpeed(lineI.getAttribute(TimeInterval.ATTR_SET_SPEED, Integer.class));
            }

            // comment
            if (nodeI.getAttributes().getBool(TimeInterval.ATTR_COMMENT_SHOWN)) {
                row.setComment(nodeI.getComment());
            }
            // check line end
            if ((nodeI.isLast() || nodeI.isInnerStop()) && this.isTrackEnd(lastLineI, nodeI)) {
                row.setLineEnd(Boolean.TRUE);
            }
            // check occupied track
            if (nodeI.getAttributes().getBool(TimeInterval.ATTR_OCCUPIED)) {
                row.setOccupied(Boolean.TRUE);
            }
            // check shunt
            if (nodeI.getAttributes().getBool(TimeInterval.ATTR_SHUNT)) {
                row.setShunt(Boolean.TRUE);
            }

            if (nodeI.getOwnerAsNode().getTracks().size() > 1)
                row.setTrack(nodeI.getTrack().getNumber());

            boolean onControlled = checkOnControlled(nodeI.getOwnerAsNode());
            if (onControlled)
                row.setOnControlled(true);

            if (lastLineI != null && lastLineI.isToStraight())
                row.setStraight(lastLineI.isToStraight());
            if (nodeI.getOwnerAsNode().getAttributes().getBool(Node.ATTR_TRAPEZOID_SIGN)) {
                Pair<Boolean, List<TranslatedString>> concurrentTrains = this.getConcurrentTrains(nodeI);
                if (concurrentTrains != null) {
                    row.setConcurrentTrains(concurrentTrains.second);
                    row.setFirstConcurrent(concurrentTrains.first);
                }
            }

            LineClass lineClass = lineI != null ? lineI.getLineClass() : null;
            if (lineClass != null)
                row.setLineClass(lineClass.getName());

            // route position
            Double routePosition = null;
            Double routePositionOut = null;
            if (lineI != null)
                routePositionOut = this.getRoutePosition(lineI.getOwnerAsLine(), nodeI.getOwnerAsNode());
            if (lastLineI != null)
                routePosition = this.getRoutePosition(lastLineI.getOwnerAsLine(), nodeI.getOwnerAsNode());
            if (routePosition == null)
                routePosition = routePositionOut;
            if (routePosition != null && routePositionOut != null &&
                    routePosition.doubleValue() == routePositionOut.doubleValue())
                routePositionOut = null;

            row.setRoutePosition(routePosition);
            row.setRoutePositionOut(routePositionOut);

            // freight
            if (nodeI.isFirst() && nodeI.isFreight()) {
                List<? extends FreightConnection> freightDests = strategy.getFreightToNodes(nodeI);
                if (!freightDests.isEmpty()) {
                    ArrayList<FreightDestinationInfo> fl = new ArrayList<>(freightDests.size());
                    for (FreightConnection dst : freightDests) {
                        fl.add(FreightDestinationInfo.convert(locale, dst));
                    }
                    row.setFreightDest(fl);
                }
            }

            timetable.getRows().add(row);

            lastLineI = lineI;
        }
    }

    private boolean checkOnControlled(Node node) {
        // get all lines for node
        boolean check = true;
        for (Line line : diagram.getNet().getLinesOf(node)) {
            check = check && Boolean.TRUE.equals(line.getAttribute(Line.ATTR_CONTROLLED, Boolean.class));
        }
        return check;
    }

    private Pair<Boolean, List<TranslatedString>> getConcurrentTrains(TimeInterval interval) {
        TrainComparator comparator = diagram.getTrainsData().getTrainComparator();
        Node node = interval.getOwnerAsNode();
        TimeInterval toBeChecked = interval;
        Train train = interval.getTrain();
        if (interval.isFirst() && train.getTimeIntervalBefore() != null)
            toBeChecked = train.getTimeIntervalBefore();
        else if (interval.isLast() && train.getTimeIntervalAfter() != null)
            toBeChecked = train.getTimeIntervalAfter();
        Set<TimeInterval> over = node.getOverlappingTimeIntervals(toBeChecked);
        boolean first = true;
        // check if the train is first in the station (start be marked with trapezoid)
        if (!interval.isFirst())
            for (Iterator<TimeInterval> i = over.iterator(); i.hasNext();) {
                TimeInterval checked = i.next();
                if (interval.getStart() > checked.getStart() || (interval.getStart() == checked.getStart()
                        && comparator.compare(interval.getTrain(), checked.getTrain()) >= 0)) {
                    first = false;
                    break;
                }
            }
        if (over.isEmpty()) {
            return null;
        } else {
            List<Train> tTrains = new ArrayList<>(over.size());
            for (TimeInterval ti : over) {
                if (!tTrains.contains(ti.getTrain()))
                    tTrains.add(ti.getTrain());
            }
            ElementSort<Train> s = new ElementSort<>(new TrainComparator(
                    node.getDiagram().getTrainsData().getTrainSortPattern()));
            tTrains = s.sort(tTrains);
            List<TranslatedString> result = new LinkedList<>();
            for (Train t : tTrains) {
                result.add(t.getName());
            }
            return new Pair<>(first, result);
        }
    }

    private Text createText(TextItem item) {
        if (item.getAttributes().getBool(TextItem.ATTR_TRAIN_TIMETABLE_INFO)) {
            return new Text(item.getName(), item.getType().getKey(), item.getText());
        } else {
            return null;
        }
    }

    private Double getRoutePosition(Line line, Node node) {
        Pair<Line, Node> pair = new Pair<>(line, node);
        Double position = null;
        if (!cachedRoutePositions.containsKey(pair)) {
            position = computeRoutePosition(pair);
            cachedRoutePositions.put(pair, position);
        } else {
            position = cachedRoutePositions.get(pair);
        }
        return position;
    }

    private boolean checkRoute(Line line, List<RouteSegment<?>> segments) {
        for (RouteSegment<?> seg : segments) {
            // sequence line - node
            if (seg instanceof Line && seg == line)
                return true;
        }
        return false;
    }

    private Double computeRoutePosition(Pair<Line, Node> pair) {
        Route foundRoute = null;
        for (Route route : diagram.getRoutes()) {
            if (route.isNetPart() && checkRoute(pair.first, route.getSegments())) {
                foundRoute = route;
                break;
            }
        }
        if (foundRoute != null) {
            // compute distance
            long length = 0;
            for (RouteSegment<?> seg : foundRoute.getSegments()) {
                if (seg == pair.second) {
                    break;
                } else if (seg instanceof Line) {
                    length += ((Line) seg).getLength();
                }
            }
            double ratio = UnitUtil.getRouteLengthRatio(diagram);
            return UnitUtil.convertRouteLenght(length, diagram, ratio);
        } else
            return null;
    }

    private boolean isTrackEnd(TimeInterval lastLineI, TimeInterval nodeI) {
        Optional<TrackConnector> fromConnector = lastLineI.getToTrackConnector();
        return fromConnector.map(conn -> {
            Side side = conn.getOrientation();
            return nodeI.getOwnerAsNode().getConnectors().stream()
                    .filter(c -> c.getOrientation() != side)
                    .flatMap(c -> c.getSwitches().stream())
                    .filter(s -> s.getNodeTrack() == nodeI.getTrack())
                    .findAny()
                    .isPresent();
        }).orElse(false);
    }
}
