package net.parostroj.timetable.output2.impl;

import java.util.*;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainSort;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.utils.Pair;

/**
 * Extracts information for train timetables.
 *
 * @author jub
 */
public class TrainTimetablesExtractor {

    private final TrainDiagram diagram;
    private final List<Train> trains;
    private final List<Route> routes;
    private final TrainsCycle cycle;
    private final Map<Pair<Line, Node>, Double> cachedRoutePositions;

    public TrainTimetablesExtractor(TrainDiagram diagram, List<Train> trains, List<Route> routes, TrainsCycle cycle) {
        this.diagram = diagram;
        this.trains = trains;
        this.routes = routes;
        this.cycle = cycle;
        this.cachedRoutePositions = new HashMap<Pair<Line, Node>, Double>();
    }

    public TrainTimetables getTrainTimetables() {
        List<TrainTimetable> result = new LinkedList<TrainTimetable>();
        List<Text> texts = null;

        // trains
        for (Train train : trains) {
            result.add(this.createTimetable(train));
        }

        // texts
        for (TextItem item : diagram.getTextItems()) {
            if (texts == null)
                texts = new LinkedList<Text>();
            texts.add(this.createText(item));
        }

        TrainTimetables timetables = new TrainTimetables(result);
        timetables.setTexts(texts);

        // routes
        timetables.setRoutes(RoutesExtractor.convert(routes));

        // route length unit
        String unit = (String)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_UNIT);
        if (unit != null && !"".equals(unit))
            timetables.setRouteLengthUnit(unit);

        // route info (lower priority)
        timetables.setRouteNumbers((String)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_NUMBERS));
        timetables.setRouteStations((String)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_NODES));
        // validity
        timetables.setValidity((String)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_VALIDITY));

        // cycle
        if (cycle != null) {
            DriverCyclesExtractor ex = new DriverCyclesExtractor(diagram, null, false);
            timetables.setCycle(ex.createCycle(cycle));
        }

        return timetables;
    }

    private TrainTimetable createTimetable(Train train) {
        TrainTimetable timetable = new TrainTimetable();
        timetable.setName(train.getName());
        timetable.setCompleteName(train.getCompleteName());
        this.extractRouteInfo(train, timetable);
        this.extractDieselElectric(train, timetable);
        if (train.oneLineHasAttribute(Line.ATTR_CONTROLLED, Boolean.TRUE))
            timetable.setControlled(true);
        WeightDataExtractor wex = new WeightDataExtractor(train);
        timetable.setWeightData(wex.getData());
        this.extractLengthData(train, timetable);
        this.extractRows(train, timetable);
        return timetable;
    }

    private void extractDieselElectric(Train train, TrainTimetable timetable) {
        if (Boolean.TRUE.equals(train.getAttribute("diesel")))
            timetable.setDiesel(true);
        if (Boolean.TRUE.equals(train.getAttribute("electric")))
            timetable.setElectric(true);
    }

    private void extractRouteInfo(Train train, TrainTimetable timetable) {
        if (train.getAttribute("route") == null) {
            return;
        }
        String result = ((TextTemplate) train.getAttribute("route")).evaluate(train);
        timetable.setRouteInfo(new LinkedList<RouteInfoPart>());
        // split
        String[] splitted = result.split("-");
        for (String sPart : splitted) {
            sPart = sPart.trim();
            timetable.getRouteInfo().add(new RouteInfoPart(sPart, null));
        }
    }

    private void extractLengthData(Train train, TrainTimetable timetable) {
        if (Boolean.TRUE.equals(train.getAttribute("show.station.length"))) {
            // compute maximal length
            Pair<Node, Integer> length = TrainsHelper.getNextLength(train.getStartNode(), train, TrainsHelper.NextType.LAST_STATION);
            if (length != null && length.second != null) {
                // get length unit
                LengthUnit lengthUnitObj = (LengthUnit) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT);
                LengthData data = new LengthData();
                data.setLength(length.second);
                data.setLengthInAxles(lengthUnitObj != null && lengthUnitObj == LengthUnit.AXLE);
                data.setLengthUnit(lengthUnitObj != null ? lengthUnitObj.getUnitsOfString() : null);
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

            if (Boolean.TRUE.equals(nodeI.getOwnerAsNode().getAttribute("control.station")))
                row.setControlStation(true);
            if ("new.signals".equals(nodeI.getOwnerAsNode().getAttribute("interlocking.plant")))
                row.setLightSignals(true);
            row.setStation(nodeI.getOwnerAsNode().getName());
            row.setStationType(nodeI.getOwnerAsNode().getType().getKey());
            if (!nodeI.isFirst())
                row.setArrival(diagram.getTimeConverter().convertIntToXml(nodeI.getStart()));
            if (!nodeI.isLast())
                row.setDeparture(diagram.getTimeConverter().convertIntToXml(nodeI.getEnd()));
            if (lineI != null) {
                row.setSpeed(lineI.getSpeed());
                row.setLineTracks(lineI.getOwnerAsLine().getTracks().size());
                row.setSetSpeed((Integer) lineI.getAttribute(TimeInterval.ATTR_SET_SPEED));
            }

            // comment
            if (Boolean.TRUE.equals(nodeI.getAttribute("comment.shown"))) {
                String comment = (String)nodeI.getAttribute("comment");
                if (comment != null && !comment.trim().equals(""))
                    row.setComment(comment);
            }
            // check line end
            if (nodeI.isLast() || nodeI.isInnerStop()) {
                if (Boolean.TRUE.equals(nodeI.getTrack().getAttribute("line.end"))) {
                    row.setLineEnd(Boolean.TRUE);
                }
            }
            // check occupied track
            if (Boolean.TRUE.equals(nodeI.getAttribute("occupied"))) {
                row.setOccupied(Boolean.TRUE);
            }
            // check shunt
            if (Boolean.TRUE.equals(nodeI.getAttribute("shunt"))) {
                row.setShunt(Boolean.TRUE);
            }

            if (nodeI.getOwnerAsNode().getTracks().size() > 1)
                row.setTrack(nodeI.getTrack().getNumber());

            boolean onControlled = checkOnControlled(nodeI.getOwnerAsNode());
            if (onControlled)
                row.setOnControlled(true);

            if (lastLineI != null && lastLineI.getToStraightTrack() != null)
                row.setStraight(lastLineI.getToStraightTrack() == nodeI.getTrack());
            if (Boolean.TRUE.equals(nodeI.getOwnerAsNode().getAttribute("trapezoid.sign"))) {
                Pair<Boolean, List<String>> trapezoidTrains = this.getTrapezoidTrains(nodeI);
                if (trapezoidTrains != null) {
                    row.setTrapezoidTrains(trapezoidTrains.second);
                    row.setTrapezoid(trapezoidTrains.first);
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

            timetable.getRows().add(row);

            lastLineI = lineI;
        }
    }

    private boolean checkOnControlled(Node node) {
        // get all lines for node
        boolean check = true;
        for (Line line : diagram.getNet().getLinesOf(node)) {
            check = check && Boolean.TRUE.equals(line.getAttribute(Line.ATTR_CONTROLLED));
        }
        return check;
    }

    private Pair<Boolean, List<String>> getTrapezoidTrains(TimeInterval interval) {
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
                if (interval.getStart() > checked.getStart()) {
                    first = false;
                    break;
                } else if (interval.getStart() == checked.getStart()) {
                    // train nearer the begining is first
                    List<Train> tl = diagram.getTrains();
                    if (tl.indexOf(interval.getTrain()) > tl.indexOf(checked.getTrain())) {
                        first = false;
                        break;
                    }
                }
            }
        if (over.isEmpty()) {
            return null;
        } else {
            List<Train> tTrains = new ArrayList<Train>(over.size());
            for (TimeInterval ti : over) {
                if (!tTrains.contains(ti.getTrain()))
                    tTrains.add(ti.getTrain());
            }
            TrainSort s = new TrainSort(new TrainComparator(TrainComparator.Type.ASC, node.getTrainDiagram().getTrainsData().getTrainSortPattern()));
            tTrains = s.sort(tTrains);
            List<String> result = new LinkedList<String>();
            for (Train t : tTrains) {
                result.add(t.getName());
            }
            return new Pair<Boolean, List<String>>(!first, result);
        }
    }

    private Text createText(TextItem item) {
        Text t = new Text(item.getName(), item.getType(), item.getText());
        return t;
    }

    private Double getRoutePosition(Line line, Node node) {
        Pair<Line, Node> pair = new Pair<Line, Node>(line, node);
        Double position = null;
        if (!cachedRoutePositions.containsKey(pair)) {
            position = computeRoutePosition(pair);
            cachedRoutePositions.put(pair, position);
        } else {
            position = cachedRoutePositions.get(pair);
        }
        return position;
    }

    private boolean checkRoute(Line line, List<RouteSegment> segments) {
        for (RouteSegment seg : segments) {
            // sequence line - node
            if (seg.asLine() != null && seg.asLine() == line)
                return true;
        }
        return false;
    }

    private Double computeRoutePosition(Pair<Line, Node> pair) {
        Route foundRoute = null;
        for (Route route : diagram.getRoutes()) {
            if (route.isNetPart()) {
                if (checkRoute(pair.first, route.getSegments())) {
                    foundRoute = route;
                    break;
                }
            }
        }
        if (foundRoute != null) {
            // compute distance
            long length = 0;
            for (RouteSegment seg : foundRoute.getSegments()) {
                if (seg.asNode() == pair.second)
                    break;
                else if (seg.asLine() != null)
                    length += seg.asLine().getLength();
            }
            Double ratio = (Double)diagram.getAttribute(TrainDiagram.ATTR_ROUTE_LENGTH_RATIO);
            if (ratio == null)
                ratio = 1.0;
            return ratio * length;
        } else
            return null;
    }
}
