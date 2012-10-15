package net.parostroj.timetable.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.Pair;

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

        // move to new time
        train.move(time);

        return train;
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
                interval = originalInterval.getOwnerAsNode().createTimeInterval(IdGenerator.getInstance().getId(), train, currentTime, originalInterval.getLength());
            } else {
                interval = originalInterval.getOwnerAsLine().createTimeInterval(IdGenerator.getInstance().getId(), train, currentTime, originalInterval.getDirection().reverse(), originalInterval.getSpeed(), 0, 0);
            }
            currentTime = interval.getEnd();
            train.addInterval(interval);
        }
        train.recalculate();

        return train;
    }

    /**
     * creates train of specified type starting on specified time.
     *
     * @param id id
     * @param name name
     * @param trainType train type
     * @param topSpeed top speed
     * @param route route
     * @param time starting time
     * @param diagram train diagram
     * @param defaultStop default stop time
     * @return created train
     */
    public Train createTrain(String id, String name, TrainType trainType, int topSpeed, Route route, int time, TrainDiagram diagram, int defaultStop) {
        Train train = diagram.createTrain(id);
        train.setNumber(name);
        train.setType(trainType);
        train.setTopSpeed(topSpeed);

        List<Pair<RouteSegment, Integer>> data = this.createDataForRoute(route);
        this.adjustSpeedsAndStops(data, train, topSpeed, defaultStop);

        int i = 0;
        TimeInterval interval;
        Node lastNode = null;

        for (Pair<RouteSegment, Integer> pair : data) {
            if (pair.first instanceof Node) {
                // handle node
                Node node = (Node)pair.first;
                interval = node.createTimeInterval(
                        IdGenerator.getInstance().getId(),
                        train, time, pair.second);
                lastNode = node;
            } else {
                // handle line
                Line line = (Line)pair.first;
                TimeIntervalDirection direction =
                        (line.getFrom() == lastNode) ?
                            TimeIntervalDirection.FORWARD :
                            TimeIntervalDirection.BACKWARD;
                interval = line.createTimeInterval(
                        IdGenerator.getInstance().getId(),
                        train, time,
                        direction, pair.second,
                        this.computeFromSpeed(pair, data, i),
                        this.computeToSpeed(pair, data, i));
            }

            // add created interval to train and set current time
            time = interval.getEnd();
            train.addInterval(interval);

            i++;
        }

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

    private int computeFromSpeed(Pair<RouteSegment,Integer> pair, List<Pair<RouteSegment, Integer>> data, int i) {
        if (!(pair.first instanceof Line))
            throw new IllegalArgumentException("Cannot find speed for node.");
        // previous node is stop - first node or node has not null time
        if ((i - 1) == 0 || data.get(i - 1).second != 0)
            return 0;
        else {
            // check speed of previous line
            return data.get(i - 2).second;
        }
    }

    private int computeToSpeed(Pair<RouteSegment,Integer> pair, List<Pair<RouteSegment, Integer>> data, int i) {
        if (!(pair.first instanceof Line))
            throw new IllegalArgumentException("Cannot find speed for node.");
        // next node is stop - last node or node has not null time
        if ((i + 1) == (data.size() - 1) || data.get(i + 1).second != 0)
            return 0;
        else {
            // check speed of previous line
            return data.get(i + 2).second;
        }
    }
}
