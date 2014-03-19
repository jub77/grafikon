package net.parostroj.timetable.actions;

import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.Tuple;

/**
 * TrainCreator creates train with specified parameters for established route.
 *
 * @author jub
 */
public class TrainBuilder {

    /**
     * creates instance..
     */
    public TrainBuilder() {
    }

    /**
     * creates new train with the same data and same route.
     *
     * @param id id
     * @param number number of the name train
     * @param time new start time
     * @param copiedTrain original train
     * @return created train
     */
    public Train createTrain(String id, String number, int time, Train copiedTrain) {
        // create new train with the same data
        Train train = copiedTrain.getTrainDiagram().createTrain(id);
        train.setNumber(number);
        train.setType(copiedTrain.getType());
        train.setDescription(copiedTrain.getDescription());
        train.setTopSpeed(copiedTrain.getTopSpeed());
        train.setAttributes(new Attributes(copiedTrain.getAttributes()));

        // create copy of time intervals
        for (TimeInterval copiedInterval : copiedTrain.getTimeIntervalList()) {
            TimeInterval interval = new TimeInterval(IdGenerator.getInstance().getId(), copiedInterval);
            // redirect to a new train
            interval.setTrain(train);

            // add interval
            train.addInterval(interval);
        }

        // recalculate
        train.recalculate();

        // move to new time
        train.move(time);

        return train;
    }

    public Train createTrain(Train copiedTrain, Route newRoute) {
        Train newTrain = createTrain(copiedTrain.getId(), copiedTrain.getNumber(), copiedTrain.getType(),
                copiedTrain.getTopSpeed(), newRoute, 0, copiedTrain.getTrainDiagram(), 0);
        // copy train properties
        newTrain.setAttributes(copiedTrain.getAttributes());
        newTrain.setDescription(copiedTrain.getDescription());
        newTrain.setTimeAfter(copiedTrain.getTimeAfter());
        newTrain.setTimeBefore(copiedTrain.getTimeBefore());
        // adjust time intervals which are to previous route parts
        Iterator<TimeInterval> oldIter = copiedTrain.getTimeIntervalList().iterator();
        Tuple<TimeInterval> first = null;
        while (oldIter.hasNext()) {
            Iterator<TimeInterval> newIter = newTrain.getTimeIntervalList().iterator();
            TimeInterval oi = oldIter.next();
            TimeInterval ni = newIter.next();
            while (ni != null && !compare(ni, oi)) {
                ni = newIter.hasNext() ? newIter.next() : null;

            }
            if (ni != null && compare(ni, oi)) {
                ni.setAddedTime(oi.getAddedTime());
                ni.setAttributes(oi.getAttributes());
                if (!ni.isFirst() && !ni.isLast())
                    ni.setLength(oi.getLength());
                ni.setSpeed(oi.getSpeed());
                ni.setTrack(oi.getTrack());
                if (first == null) {
                    first = new Tuple<TimeInterval>(ni, oi);
                }
            }
        }
        newTrain.recalculate();
        // move if there is a common point
        if (first != null) {
            newTrain.move(first.second.getEnd() - first.first.getEnd());
        }
        return newTrain;
    }

    private boolean compare(TimeInterval i1, TimeInterval i2) {
        if (i1.getOwner() == i2.getOwner()) {
            if (i1.isNodeOwner())
                return true;
            else
                return i1.getDirection() == i2.getDirection();
        } else {
            return false;
        }
    }

    public Train createReverseTrain(String id, String number, int time, Train copiedTrain) {
        // create train
        Train train = copiedTrain.getTrainDiagram().createTrain(id);
        train.setNumber(number);
        train.setType(copiedTrain.getType());
        train.setDescription(copiedTrain.getDescription());
        train.setTopSpeed(copiedTrain.getTopSpeed());
        train.setAttributes(new Attributes(copiedTrain.getAttributes()));

        // get original intervals in reverse order
        LinkedList<TimeInterval> reverseIntervals = new LinkedList<TimeInterval>();
        for (TimeInterval interval : copiedTrain.getTimeIntervalList()) {
            reverseIntervals.addFirst(interval);
        }

        int currentTime = time;

        // create time intervals
        for (TimeInterval originalInterval : reverseIntervals) {
            TimeInterval interval = null;
            if (originalInterval.isNodeOwner()) {
                interval = new TimeInterval(IdGenerator.getInstance().getId(), train, originalInterval.getOwner(),
                        currentTime, currentTime + originalInterval.getLength(), null);
            } else {
                interval = new TimeInterval(IdGenerator.getInstance().getId(), train, originalInterval.getOwner(),
                        0, 0, originalInterval.getSpeed(), originalInterval.getDirection(), null,
                        originalInterval.getAddedTime());
            }
            currentTime = interval.getEnd();
            train.addInterval(interval);
        }
        train.recalculate();
        train.assignEmptyTracks();

        return train;
    }

    /**
     * creates train of specified type starting on specified time.
     *
     * @param id id
     * @param number name
     * @param trainType train type
     * @param topSpeed top speed
     * @param route route
     * @param time starting time
     * @param diagram train diagram
     * @param defaultStop default stop time
     * @return created train
     */
    public Train createTrain(String id, String number, TrainType trainType, int topSpeed, Route route, int time,
            TrainDiagram diagram, int defaultStop) {
        Train train = diagram.createTrain(id);
        train.setNumber(number);
        train.setType(trainType);
        train.setTopSpeed(topSpeed);

        List<Pair<RouteSegment, Integer>> data = this.createDataForRoute(route);
        this.adjustSpeedsAndStops(data, train, topSpeed, defaultStop);

        Node lastNode = null;

        for (Pair<RouteSegment, Integer> pair : data) {
            TimeInterval interval = null;
            if (pair.first instanceof Node) {
                // handle node
                Node node = (Node)pair.first;
                interval = new TimeInterval(IdGenerator.getInstance().getId(), train, node, time,
                        time + pair.second, null);
                lastNode = node;
            } else {
                // handle line
                Line line = (Line)pair.first;
                TimeIntervalDirection direction =
                        (line.getFrom() == lastNode) ?
                            TimeIntervalDirection.FORWARD :
                            TimeIntervalDirection.BACKWARD;
                interval = new TimeInterval(IdGenerator.getInstance().getId(), train, line, time, time, pair.second, direction, null, 0);
            }

            // add created interval to train and set current time
            time = interval.getEnd();
            train.addInterval(interval);
        }
        train.recalculate();
        train.assignEmptyTracks();

        return train;
    }

    private List<Pair<RouteSegment, Integer>> createDataForRoute(Route route) {
        List<Pair<RouteSegment, Integer>> data = new ArrayList<Pair<RouteSegment, Integer>>(route.getSegments().size());
        for (RouteSegment segment : route.getSegments()) {
            data.add(new Pair<RouteSegment, Integer>(segment, 0));
        }
        return data;
    }

    private void adjustSpeedsAndStops(List<Pair<RouteSegment, Integer>> data, Train train, int speed, int defaultStop) {
        int size = data.size();
        int i = 0;
        for (Pair<RouteSegment,Integer> pair : data) {
            i++;
            if (pair.first instanceof Node) {
                // node
                Node node = (Node)pair.first;
                if (i != 1 && i != size && node.getType() != NodeType.ROUTE_SPLIT && node.getType() != NodeType.SIGNAL) {
                    // set default stop
                    pair.second = defaultStop;
                }
            } else {
                // line
                Line line = (Line)pair.first;
                pair.second = line.computeSpeed(train, null, speed);
            }
        }
    }
}
