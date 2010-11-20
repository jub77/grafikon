package net.parostroj.timetable.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.utils.Pair;
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

    /**
     * returns weight for specified time interval based on line and engine class.
     * It returns <code>null</code> if the weight is not specified.
     *
     * @param interval time interval
     * @return weight
     */
    public static Integer getWeight(TimeInterval interval) {
        Pair<Integer, List<TrainsCycleItem>> weightAndCycle = getWeightAndCycles(interval);
        if (weightAndCycle == null || weightAndCycle.first == null)
            return null;
        else
            return weightAndCycle.first;
    }

    /**
     * returns weight for interval. It returns old style weight if the
     * weight table info is not specified.
     *
     * @param interval time interval
     * @return weight
     */
    public static Integer getWeightWithAttribute(TimeInterval interval) {
        Integer weight = getWeightFromAttribute(interval.getTrain());
        if (weight == null) {
            weight = getWeight(interval);
        }
        return weight;
    }

    /** Pattern for extracting weight info. */
    private static Pattern NUMBER = Pattern.compile("\\d+");

    /**
     * return converted weight from weight attribute.
     *
     * @param train train
     * @return weight
     */
    public static Integer getWeightFromAttribute(Train train) {
        return (Integer) train.getAttribute("weight");
    }

    /**
     * return converted weight from weight.info attribute.
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
        List<EngineClass> engines = getEngineClasses(items);
        if (lineClass != null && !engines.isEmpty()) {
            // compute weight
            int weight = 0;
            for (EngineClass engine : engines) {
                weight += engine.getWeightTableRowForSpeed(interval.getSpeed()).getWeight(lineClass);
            }
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
            // number of axles should be an even number
            length = length - (length % 2);
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
    public static Integer convertWeightToLength(Train train, TrainDiagram diagram, Integer weight) {
        if (weight == null)
            return null;
        // weight in kg
        Integer wpa = Boolean.TRUE.equals(train.getAttribute("empty")) ?
            (Integer) diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY) :
            (Integer) diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE);
        LengthUnit lu = (LengthUnit) diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT);
        // length in mm
        double axles = (weight * 1000) / wpa;
        Integer result = null;
        if (lu == LengthUnit.AXLE) {
            // number of axles should be an even number
            result = (int) axles;
            result = result - (result % 2);
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

    public static List<EngineClass> getEngineClasses(List<TrainsCycleItem> list) {
        List<EngineClass> result = Collections.emptyList();
        if (list.size() == 1) {
            EngineClass eClass = getEngineClass(list.get(0));
            if (eClass != null)
                result = Collections.singletonList(eClass);
        } else {
            result = new LinkedList<EngineClass>();
            for (TrainsCycleItem item : list) {
                EngineClass eClass = getEngineClass(item);
                if (eClass != null)
                    result.add(eClass);
            }
        }
        return result;
    }

    /**
     * extracts engine class from cycle item.
     *
     * @param item cycle item
     * @return cycle item
     */
    public static EngineClass getEngineClass(TrainsCycleItem item) {
        return (EngineClass) item.getCycle().getAttribute("engine.class");
    }

    /**
     * return list with weights for the train. It returns <code>null</code> if
     * the weight is not specified for all parts.
     *
     * @param train train
     * @return list with weights
     */
    public static List<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> getWeightList(Train train) {
        // if the train is not coverred return null
        if (!train.isCovered(TrainsCycleType.ENGINE_CYCLE))
            return null;
        List<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> result = new ArrayList<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>>();
        for (TimeInterval interval : train.getTimeIntervalList()) {
            if (interval.isNodeOwner())
                result.add(new Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>(interval, null));
            else {
                Pair<Integer, List<TrainsCycleItem>> weight = getWeightAndCycles(interval);
                if (weight == null)
                    return null;
                else {
                    result.add(new Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>(interval, weight));
                }
            }
        }

        return result;
    }

    /**
     * returns list of lengths. It returns <code>null</code> if the weight is not
     * specified for all parts of the route of the train.
     *
     * @param train train
     * @param diagram trains diagram
     * @return list of lengths
     */
    public static List<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> getLengthList(Train train, TrainDiagram diagram) {
        List<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> result = getWeightList(train);
        convertWeightToLength(result, diagram);
        return result;
    }

    /**
     * converts weights to lengths in the list.
     *
     * @param list list of weights
     * @param diagram trains diagram
     */
    public static void convertWeightToLength(List<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> list, TrainDiagram diagram) {
        for (Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>> pair : list) {
            convertWeightToLength(pair.first.getTrain(), diagram, pair.second.first);
        }
    }

    /**
     * returns weight for the next node. It returns pair with node and weight. It
     * returns <code>null</code> if the weights are not specified. As the next node
     * is taken the end of the route or the nearest branch station.
     *
     * @param node starting node
     * @param train train
     * @param nextType next type
     * @return next node and available weight
     */
    public static Pair<Node, Integer> getNextWeight(Node node, Train train, NextType nextType) {
        List<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> weightList = getWeightList(train);
        Integer retValue = null;
        Node retNode = null;
        if (weightList != null) {
            Iterator<Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>>> i = weightList.iterator();
            // skip to node
            while (i.hasNext()) {
                Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>> pairI = i.next();
                // node found
                if (pairI.first.getOwner() == node)
                    break;
            }
            // check weight
            while (i.hasNext()) {
                Pair<TimeInterval, Pair<Integer, List<TrainsCycleItem>>> pairI = i.next();
                if (pairI.first.isLineOwner()) {
                    if (retValue == null || retValue > pairI.second.first)
                        retValue = pairI.second.first;
                } else if (pairI.first.isNodeOwner()) {
                    retNode = pairI.first.getOwnerAsNode();
                    // next stop found
                    boolean shouldBreak = false;
                    switch (nextType) {
                        case BRANCH_STATION:
                            if (pairI.first.isStop() && retNode.getType() == NodeType.STATION_BRANCH)
                                shouldBreak = true;
                            break;
                        case FIRST_STATION:
                            if (pairI.first.isStop() && retNode.getType().isStation())
                                shouldBreak = true;
                            break;
                    }
                    if (shouldBreak)
                        break;
                }
            }
        }
        return retValue == null ? null : new Pair<Node, Integer>(retNode, retValue);
    }

    /**
     * returns length for the next node. It returns pair with node and length. It
     * returns <code>null</code> if the weights are not specified. As the next node
     * is taken the end of the route or the nearest branch station.
     *
     * @param node starting node
     * @param train train
     * @param next type
     * @return next node and available weight
     */
    public static Pair<Node, Integer> getNextLength(Node node, Train train, TrainDiagram diagram, NextType nextType) {
        Pair<Node, Integer> result = getNextWeight(node, train, nextType);
        if (result != null) {
            result.second = convertWeightToLength(train, diagram, result.second);
        }
        return result;
    }

    /**
     * updates length to the next station. It considers lengths of the stations and stops
     * (stops for the trains that need platforms). As the next station is taken
     * end of the route or the nearest branch station.
     *
     * @param node starting node
     * @param train train
     * @param length precalculated length
     * @return updated length
     */
    public static Integer updateNextLengthWithStationLengths(Node node, Train train, Integer length) {
        Iterator<TimeInterval> i = train.getTimeIntervalList().iterator();
        // look for current node
        while (i.hasNext()) {
            TimeInterval interval = i.next();
            if (interval.isNodeOwner()) {
                if (interval.getOwnerAsNode() == node) {
                    if (shouldCheckLength(node, train))
                        length = updateWithStationLength(node, length);
                    break;
                }
            }
        }
        // check next stop
        while (i.hasNext()) {
            TimeInterval interval = i.next();
            if (interval.isNodeOwner()) {
                if (interval.isStop()) {
                    if (shouldCheckLength(interval.getOwnerAsNode(), train))
                        length = updateWithStationLength(interval.getOwnerAsNode(), length);
                    if (interval.getOwnerAsNode().getType() == NodeType.STATION_BRANCH)
                        break;
                }
            }
        }
        return length;
    }

    /**
     * updates length with station length. The update is applied always without
     * consideration for the node type. The only consideration is existence of
     * station length.
     *
     * @param node node
     * @param length precalculated length
     * @return updated length
     */
    public static Integer updateWithStationLength(Node node, Integer length) {
        Integer nodeLength = convertLength(node.getTrainDiagram(), (Integer)node.getAttribute("length"));
        if (nodeLength != null && nodeLength < length)
            return nodeLength;
        else
            return length;
    }

    /**
     * checks if the length of the station should be considered in calculation.
     * The node has to be station or stop in case the train needs platform.
     *
     * @param node node
     * @param train train
     * @return if the length should be taken into account
     */
    public static boolean shouldCheckLength(Node node, Train train) {
        return node.getType().isStation() || (node.getType().isStop() && train.getType().isPlatform());
    }
}
