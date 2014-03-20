package net.parostroj.timetable.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.ResultList;
import net.parostroj.timetable.utils.Triplet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper actions for trains.
 *
 * @author jub
 */
public class TrainsHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TrainsHelper.class.getName());

    public static enum NextType {
        LAST_STATION, FIRST_STATION, BRANCH_STATION;
    }

    public static Integer getLength(TimeInterval interval) {
        Integer length = null;
        if (interval.isNodeOwner()) {
            Node node = interval.getOwnerAsNode();
            if (shouldCheckLength(node, interval.getTrain(), interval) && interval.isStop()) {
                length = (Integer) node.getAttribute(Node.ATTR_LENGTH);
                length = convertLength(node.getTrainDiagram(), length);
            }
        } else {
            length = convertWeightToLength(interval.getTrain(), getWeight(interval));
        }
        return length;
    }

    /**
     * returns weight for specified time interval based on line and engine class.
     * It returns <code>null</code> if the weight is not specified.
     *
     * @param interval time interval
     * @return weight
     */
    public static Integer getWeightFromLineAndEngine(TimeInterval interval) {
        Pair<Integer, List<TrainsCycleItem>> weightAndCycle = getWeightAndCycles(interval);
        if (weightAndCycle == null || weightAndCycle.first == null)
            return null;
        else
            return weightAndCycle.first;
    }

    /**
     * returns weight for interval. Weight specified in train has higher priority.
     *
     * @param interval time interval
     * @return weight
     */
    public static Integer getWeight(TimeInterval interval) {
        Integer weight = getWeightFromAttribute(interval.getTrain());
        if (weight == null) {
            weight = getWeightFromLineAndEngine(interval);
        }
        return weight;
    }

    /** Pattern for extracting weight info. */
    private static Pattern NUMBER = Pattern.compile("\\d+");

    /**
     * return weight from weight attribute.
     *
     * @param train train
     * @return weight
     */
    public static Integer getWeightFromAttribute(Train train) {
        return (Integer) train.getAttribute(Train.ATTR_WEIGHT);
    }

    /**
     * return converted weight from weight.info attribute.
     * Deprecated - only for loading old versions.
     *
     * @param train train
     * @return weight
     */
    public static Integer getWeightFromInfoAttribute(Train train) {
        Integer weight = null;
        String weightStr = (String) train.getAttribute("weight.info");
        // try to convert weight string to number
        if (weightStr != null) {
            try {
                Matcher matcher = NUMBER.matcher(weightStr);
                if (matcher.find()) {
                    String number = matcher.group(0);
                    weight = Integer.valueOf(number);
                }
            } catch (NumberFormatException e) {
                LOG.debug("Cannot convert weight to number: {}", weightStr);
            }
        }
        return weight;
    }

    /**
     * returns weight and train cycle items for specified time interval based
     * on line and engine class. It returns <code>null</code> if the weight is
     * not specified.
     *
     * @param interval time interval
     * @return weight and train cycle items
     */
    public static Pair<Integer, List<TrainsCycleItem>> getWeightAndCycles(TimeInterval interval) {
        if (!interval.isLineOwner()) {
            throw new IllegalArgumentException("Weight can be returned only for line interval.");
        }
        Pair<Integer, List<TrainsCycleItem>> retValue = null;
        LineClass lineClass = interval.getLineClass();
        List<TrainsCycleItem> items = getEngineCyclesForInterval(interval);
        if (lineClass != null) {
            // compute weight
            Integer weight = null;
            for (TrainsCycleItem item : items) {
                EngineClass engine = getEngineClass(item);
                if (engine != null) {
                    if (weight == null)
                        weight = 0;
                    weight += engine.getWeightTableRowForSpeed(interval.getSpeed()).getWeight(lineClass);
                }
            }
            if (weight != null)
                retValue = new Pair<Integer, List<TrainsCycleItem>>(weight, items);
        }
        return retValue;
    }

    /**
     * converts length in mm to unit specified by train diagram.
     *
     * @param diagram train diagram
     * @param length length
     * @return converted length
     */
    public static Integer convertLength(TrainDiagram diagram, Integer length) {
        if (length == null)
            return null;
        LengthUnit lengthUnit = (LengthUnit) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT);
        if (lengthUnit == LengthUnit.AXLE) {
            Integer lpa = (Integer) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE);
            Scale scale = (Scale) diagram.getAttribute(TrainDiagram.ATTR_SCALE);
            length = (length * scale.getRatio()) / lpa;
        } else if (lengthUnit != null) {
            BigDecimal converted = lengthUnit.convertFrom(new BigDecimal(length), LengthUnit.MM);
            try {
                length = UnitUtil.convert(converted);
            } catch (ArithmeticException e) {
                LOG.warn("Couldn't convert value {} to {}.", length, lengthUnit.getKey());
                LOG.warn(e.getMessage());
            }
        }
        return length;
    }

    /**
     * converts weight to length based on conversion ratio and state of the train empty/loaded.
     *
     * @param train train
     * @param diagram trains diagram
     * @param weight weight
     * @return length
     */
    public static Integer convertWeightToLength(Train train, Integer weight) {
        if (weight == null)
            return null;
        // weight in kg
        TrainDiagram diagram = train.getTrainDiagram();
        Integer wpa = Boolean.TRUE.equals(train.getAttribute(Train.ATTR_EMPTY)) ?
            (Integer) diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY) :
            (Integer) diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE);
        LengthUnit lu = (LengthUnit) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT);
        // length in mm
        double axles = (double) (weight * 1000) / wpa;
        Integer result = null;
        if (lu == LengthUnit.AXLE) {
            // number of axles should be an even number
            result = (int) axles;
        } else {
            Integer lpa = (Integer) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE);
            Scale scale = (Scale) diagram.getAttribute(TrainDiagram.ATTR_SCALE);
            // length in mm
            result = (int)(axles * lpa);
            // adjust by scale
            result = result / scale.getRatio();
            // convert to unit
            BigDecimal converted = lu.convertFrom(new BigDecimal(result), LengthUnit.MM);
            try {
                result = UnitUtil.convert(converted);
            } catch (ArithmeticException e) {
                LOG.warn("Couldn't convert value {} to {}.", result, lu.getKey());
                LOG.warn(e.getMessage());
                result = null;
            }
        }
        return result;
    }

    /**
     * returns engine cycle items for interval.
     *
     * @param interval time interval
     * @return list of engine cycle items
     */
    public static List<TrainsCycleItem> getEngineCyclesForInterval(TimeInterval interval) {
        Train train = interval.getTrain();
        return train.getCycleItemsForInterval(TrainsCycleType.ENGINE_CYCLE, interval);
    }

    /**
     * returns list of engine classes for time interval.
     *
     * @param interval time interval
     * @return list of engine classes
     */
    public static List<EngineClass> getEngineClasses(TimeInterval interval) {
        List<TrainsCycleItem> list = getEngineCyclesForInterval(interval);
        return getEngineClasses(list);
    }

    /**
     * returns list of extracted engine classes from list of cycle items.
     *
     * @param list list of cycle items
     * @return list of engines
     */
    public static List<EngineClass> getEngineClasses(List<TrainsCycleItem> list) {
        ResultList<EngineClass> result = new ResultList<EngineClass>();
        for (TrainsCycleItem item : list) {
            EngineClass eClass = getEngineClass(item);
            if (eClass != null)
                result.add(eClass);
        }
        return result.get();
    }

    /**
     * extracts engine class from cycle item.
     *
     * @param item cycle item
     * @return cycle item
     */
    public static EngineClass getEngineClass(TrainsCycleItem item) {
        return (EngineClass) item.getCycle().getAttribute(TrainsCycle.ATTR_ENGINE_CLASS);
    }

    /**
     * return list with weights for the train.
     *
     * @param train train
     * @return list with weights
     */
    public static List<Triplet<TimeInterval, Integer, List<TrainsCycleItem>>> getWeightList(Train train) {
        List<TimeInterval> intervals = train.getTimeIntervalList();
        List<Triplet<TimeInterval, Integer, List<TrainsCycleItem>>> result =
            new ArrayList<Triplet<TimeInterval, Integer, List<TrainsCycleItem>>>(intervals.size());
        for (TimeInterval interval : intervals) {
            if (interval.isNodeOwner())
                result.add(new Triplet<TimeInterval, Integer, List<TrainsCycleItem>>(interval, null, null));
            else {
                Integer weight = getWeight(interval);
                List<TrainsCycleItem> cycles = getEngineCyclesForInterval(interval);
                result.add(new Triplet<TimeInterval, Integer, List<TrainsCycleItem>>(interval, weight, cycles));
            }
        }
        return result;
    }

    /**
     * returns list of lengths.
     *
     * @param train train
     * @param diagram trains diagram
     * @return list of lengths
     */
    public static List<Triplet<TimeInterval, Integer, List<TrainsCycleItem>>> getLengthList(Train train) {
        List<TimeInterval> intervals = train.getTimeIntervalList();
        List<Triplet<TimeInterval, Integer, List<TrainsCycleItem>>> result =
            new ArrayList<Triplet<TimeInterval, Integer, List<TrainsCycleItem>>>(intervals.size());
        for (TimeInterval interval : intervals) {
            Integer length = getLength(interval);
            List<TrainsCycleItem> cycles = interval.isLineOwner() ? getEngineCyclesForInterval(interval) : null;
            result.add(new Triplet<TimeInterval, Integer, List<TrainsCycleItem>>(interval, length, cycles));
        }
        return result;
    }

    /**
     * returns length for the next node. It returns pair with node and length. It
     * returns <code>null</code> if the weights are not specified. As the next node
     * is taken a node according to given type.
     *
     * @param node starting node
     * @param train train
     * @param next type
     * @return next node and available weight
     */
    public static Pair<Node, Integer> getNextLength(Node node, Train train, NextType nextType) {
        Node endNode = getNextNodeByType(node, train, nextType);
        Integer length = getLengthFromTo(node, endNode, train);
        return length == null ? null : new Pair<Node, Integer>(endNode, length);
    }

    /**
     * returns length minimal length from one node to another.
     *
     * @param from node from
     * @param to node to
     * @param train train
     * @return allowed length
     */
    public static Integer getLengthFromTo(Node from, Node to, Train train) {
        Integer length = null;
        // skip nodes
        ListIterator<TimeInterval> iterator = train.getTimeIntervalList().listIterator();
        while (iterator.hasNext()) {
            TimeInterval interval = iterator.next();
            if (interval.getOwner() == from) {
                iterator.previous();
                break;
            }
        }
        while (iterator.hasNext()) {
            TimeInterval interval = iterator.next();
            Integer tempLength = getLength(interval);
            if (length == null || (tempLength != null && tempLength < length))
                length = tempLength;

            if (interval.getOwner() == to)
                break;
        }
        return length;
    }

    /**
     * checks if the node of the interval fulfills search criteria.
     *
     * @param interval time interval
     * @param type type
     * @return if the criteria are met
     */
    public static boolean isNextTypeNode(TimeInterval interval, NextType type) {
        if (interval == null || !interval.isNodeOwner())
            throw new IllegalArgumentException("Wrong interval parameter: " + interval);
        Node node = interval.getOwnerAsNode();
        if (interval.isStop()) {
            switch (type) {
                case BRANCH_STATION:
                    return node.getType() == NodeType.STATION_BRANCH;
                case FIRST_STATION:
                    return node.getType().isStation();
                default:
                    break;
            }
            // last station is always true regardless of the type (including LAST_STATION type)
            return interval.isLast();
        }
        return false;
    }

    /**
     * returns next node with specified type.
     *
     * @param node starting node
     * @param train train
     * @param type type
     * @return found node
     */
    public static Node getNextNodeByType(Node node, Train train, NextType type) {
        Iterator<TimeInterval> iterator = train.getTimeIntervalList().iterator();
        while (iterator.hasNext())
            if (iterator.next().getOwner() == node)
                break;
        while (iterator.hasNext()) {
            TimeInterval i = iterator.next();
            if (i.isNodeOwner() && isNextTypeNode(i, type))
                return i.getOwnerAsNode();
        }
        return null;
    }

    /**
     * checks if the length of the station should be considered in calculation.
     * The node has to be station or stop in case the train needs platform.
     *
     * @param node node
     * @param train train
     * @param interval time interval
     * @return if the length should be taken into account
     */
    public static boolean shouldCheckLength(Node node, Train train, TimeInterval interval) {
        boolean ignore = Boolean.TRUE.equals(interval.getAttribute(TimeInterval.ATTR_IGNORE_LENGTH));
        return (node.getType().isStation() || (node.getType().isStop() && train.getType().isPlatform())) && !ignore;
    }
}
