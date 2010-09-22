package net.parostroj.timetable.output2.impl;

import java.util.*;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainSort;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.util.RoutesExtractor;
import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.utils.Pair;

/**
 * Extracts information for train timetables.
 *
 * @author jub
 */
public class TrainTimetablesExtractor {

    private TrainDiagram diagram;
    private List<Train> trains;
    private List<Route> routes;
    private Map<Pair<Line, Node>, Double> cachedRoutePositions;

    public TrainTimetablesExtractor(TrainDiagram diagram, List<Train> trains, List<Route> routes) {
        this.diagram = diagram;
        this.trains = trains;
        this.routes = routes;
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
        String unit = (String)diagram.getAttribute("route.length.unit");
        if (unit != null && !"".equals(unit))
            timetables.setRouteLengthUnit(unit);

        // validity
        timetables.setValidity((String)diagram.getAttribute("route.validity"));

        return timetables;
    }

    private TrainTimetable createTimetable(Train train) {
        TrainTimetable timetable = new TrainTimetable();
        timetable.setName(train.getName());
        timetable.setCompleteName(train.getCompleteName());
        this.extractRouteInfo(train, timetable);
        this.extractDieselElectric(train, timetable);
        if (train.oneLineHasAttribute("line.controlled", Boolean.TRUE))
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
        if (train.getAttribute("route.info") == null || ((String)train.getAttribute("route.info")).trim().equals("")) {
            return;
        }
        String result = (String) train.getAttribute("route.info");
        timetable.setRouteInfo(new LinkedList<RouteInfoPart>());
        // split
        String[] splitted = result.split("-");
        for (String sPart : splitted) {
            sPart = sPart.trim();
            if ("$1".equals(sPart)) {
                timetable.getRouteInfo().add(new RouteInfoPart(train.getStartNode().getName(), true));
            } else if ("$2".equals(sPart)) {
                timetable.getRouteInfo().add(new RouteInfoPart(train.getEndNode().getName(), true));
            } else if ("$".equals(sPart)) {
                timetable.getRouteInfo().add(new RouteInfoPart(train.getStartNode().getName(), true));
                timetable.getRouteInfo().add(new RouteInfoPart(train.getEndNode().getName(), true));
            } else {
                timetable.getRouteInfo().add(new RouteInfoPart(sPart, null));
            }
        }
    }

    private void extractLengthData(Train train, TrainTimetable timetable) {
        if (Boolean.TRUE.equals(train.getAttribute("show.station.length"))) {
            // compute maximal length
            List<Integer> lengths = new LinkedList<Integer>();
            for (TimeInterval interval : train.getTimeIntervalList()) {
                if (interval.isNodeOwner() && interval.isStop() && TrainsHelper.shouldCheckLength(interval.getOwnerAsNode(), train))
                    lengths.add((Integer)interval.getOwnerAsNode().getAttribute("length"));
            }
            // check if all lengths are set and choose the minimum
            Integer minLength = null;
            for (Integer sLength : lengths) {
                if (sLength == null)
                    return;
                else {
                    if (minLength == null || sLength.intValue() < minLength.intValue())
                        minLength = sLength;
                }
            }
            // get length unit
            String lengthUnit = null;
            boolean lengthInAxles = false;
            if (Boolean.TRUE.equals(diagram.getAttribute("station.length.in.axles"))) {
                lengthInAxles = true;
            } else {
                lengthUnit = (String)diagram.getAttribute("station.length.unit");
            }
            LengthData data = new LengthData();
            data.setLength(minLength);
            data.setLengthInAxles(lengthInAxles);
            data.setLengthUnit(lengthUnit);
            timetable.setLengthData(data);
        }
    }

    private void extractRows(Train train, TrainTimetable timetable) {
        timetable.setRows(new LinkedList<TrainTimetableRow>());
        Iterator<TimeInterval> i = train.getTimeIntervalList().iterator();
        TimeInterval lastLineI = null;
        while (i.hasNext()) {
            TimeInterval nodeI = i.next();
            TimeInterval lineI = i.hasNext() ? i.next() : null;

            if (nodeI.getOwnerAsNode().getType() == NodeType.SIGNAL)
                continue;

            TrainTimetableRow row = new TrainTimetableRow();

            if (Boolean.TRUE.equals(nodeI.getOwnerAsNode().getAttribute("control.station")))
                row.setControlStation(true);
            if ("new.signals".equals(nodeI.getOwnerAsNode().getAttribute("interlocking.plant")))
                row.setLightSignals(true);
            row.setStation(nodeI.getOwnerAsNode().getName());
            row.setStationType(nodeI.getOwnerAsNode().getType().getKey());
            if (!nodeI.isFirst())
                row.setArrival(TimeConverter.convertFromIntToText(nodeI.getStart()));
            if (!nodeI.isLast())
                row.setDeparture(TimeConverter.convertFromIntToText(nodeI.getEnd()));
            if (lineI != null)
                row.setSpeed(lineI.getSpeed());

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
                row.setTrapezoidTrains(this.getTrapezoidTrains(nodeI));
            }

            LineClass lineClass = lineI != null ? (LineClass) lineI.getOwnerAsLine().getAttribute("line.class") : null;
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
            check = check && (Boolean)line.getAttribute("line.controlled");
        }
        return check;
    }

    private List<String> getTrapezoidTrains(TimeInterval interval) {
        Node node = interval.getOwnerAsNode();
        Set<TimeInterval> over = node.getOverlappingTimeIntervals(interval);
        // filter out ...
        for (Iterator<TimeInterval> i = over.iterator(); i.hasNext();) {
            TimeInterval checked = i.next();
            if (interval.getStart() < checked.getStart()) {
                i.remove();
            }
        }
        if (over.isEmpty()) {
            return null;
        } else {
            List<Train> tTrains = new ArrayList<Train>(over.size());
            for (TimeInterval ti : over) {
                tTrains.add(ti.getTrain());
            }
            TrainSort s = new TrainSort(new TrainComparator(TrainComparator.Type.ASC, node.getTrainDiagram().getTrainsData().getTrainSortPattern()));
            tTrains = s.sort(tTrains);
            List<String> result = new LinkedList<String>();
            for (Train t : tTrains) {
                result.add(t.getName());
            }
            return result;
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
            Double ratio = (Double)diagram.getAttribute("route.length.ratio");
            if (ratio == null)
                ratio = 1.0;
            return ratio * length;
        } else
            return null;
    }
}
