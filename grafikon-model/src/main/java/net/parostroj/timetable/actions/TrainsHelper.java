package net.parostroj.timetable.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.parostroj.timetable.model.*;
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

    /**
     * returns weight for specified time interval based on line and engine class.
     * It returns <code>null</code> if the weight is not specified.
     *
     * @param interval time interval
     * @return weight
     */
    public static Integer getWeight(TimeInterval interval) {
        Pair<Integer, TrainsCycleItem> weightAndCycle = getWeightAndCycle(interval);
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
        Integer weight = getWeight(interval);
        if (weight == null) {
            weight = getWeightFromAttribute(interval.getTrain());
        }
        return weight;
    }

    /** Pattern for extracting weight info. */
    private static Pattern NUMBER = Pattern.compile("\\d+");

    /**
     * return converted weight from weight.info attribute.
     *
     * @param train train
     * @return weight
     */
    public static Integer getWeightFromAttribute(Train train) {
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
     * returns weight and train cycle item for specified time interval based
     * on line and engine class. It returns <code>null</code> if the weight is
     * not specified.
     *
     * @param interval time interval
     * @return weight and train cycle item
     */
    public static Pair<Integer, TrainsCycleItem> getWeightAndCycle(TimeInterval interval) {
        if (!interval.isLineOwner()) {
            throw new IllegalArgumentException("Weight can be returned only for line interval.");
        }
        Pair<Integer, TrainsCycleItem> retValue = null;
        LineClass lineClass = (LineClass) interval.getOwnerAsLine().getAttribute("line.class");
        Pair<EngineClass, TrainsCycleItem> engineClass = getEngineClassAndCycle(interval);
        if (lineClass != null && engineClass != null && engineClass.first != null) {
            retValue = new Pair<Integer, TrainsCycleItem>(engineClass.first.getWeightTableRowForSpeed(interval.getSpeed()).getWeight(lineClass), engineClass.second);
        }
        return retValue;
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
        Double ratio = Boolean.TRUE.equals(train.getAttribute("empty")) ?
            (Double)diagram.getAttribute("weight.ratio.empty") :
            (Double)diagram.getAttribute("weight.ratio.loaded");
        if (ratio == null || weight == null)
            return null;
        int result = (int)(weight * ratio);
        // number of axles should be an even number
        if (Boolean.TRUE.equals(diagram.getAttribute("station.length.in.axles"))) {
            result = result - (result % 2);
        }
        return result;
    }

    /**
     * returns engine class and train cycle item for time interval. It returns
     * <code>null</code> if the engine cycle is not specified.
     *
     * @param interval time interval
     * @return engine class and train cycle item
     */
    public static Pair<EngineClass, TrainsCycleItem> getEngineClassAndCycle(TimeInterval interval) {
        Train train = interval.getTrain();
        TrainsCycleItem item = train.getCycleItemForInterval(TrainsCycleType.ENGINE_CYCLE, interval);
        if (item != null) {
            return new Pair<EngineClass, TrainsCycleItem>((EngineClass) item.getCycle().getAttribute("engine.class"), item);
        }
        return null;
    }

    /**
     * returns engine class for time interval. It returns <code>null</code>
     * if the engine class is not specified.
     *
     * @param interval time interval
     * @return
     */
    public static EngineClass getEngineClass(TimeInterval interval) {
        Pair<EngineClass, TrainsCycleItem> pair = getEngineClassAndCycle(interval);
        if (pair != null)
            return pair.first;
        else
            return null;
    }

    /**
     * return list with weights for the train. It returns <code>null</code> if
     * the weight is not specified for all parts.
     *
     * @param train train
     * @return list with weights
     */
    public static List<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>> getWeightList(Train train) {
        // if the train is not coverred return null
        if (!train.isCovered(TrainsCycleType.ENGINE_CYCLE))
            return null;
        List<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>> result = new ArrayList<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>>();
        for (TimeInterval interval : train.getTimeIntervalList()) {
            if (interval.isNodeOwner())
                result.add(new Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>(interval, null));
            else {
                Pair<Integer, TrainsCycleItem> weight = getWeightAndCycle(interval);
                if (weight == null)
                    return null;
                else {
                    result.add(new Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>(interval, weight));
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
    public static List<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>> getLengthList(Train train, TrainDiagram diagram) {
        List<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>> result = getWeightList(train);
        convertWeightToLength(result, diagram);
        return result;
    }

    /**
     * converts weights to lengths in the list.
     *
     * @param list list of weights
     * @param diagram trains diagram
     */
    public static void convertWeightToLength(List<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>> list, TrainDiagram diagram) {
        for (Pair<TimeInterval, Pair<Integer, TrainsCycleItem>> pair : list) {
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
     * @return next node and available weight
     */
    public static Pair<Node, Integer> getNextWeight(Node node, Train train) {
        List<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>> weightList = getWeightList(train);
        Integer retValue = null;
        Node retNode = null;
        if (weightList != null) {
            Iterator<Pair<TimeInterval, Pair<Integer, TrainsCycleItem>>> i = weightList.iterator();
            // skip to node
            while (i.hasNext()) {
                Pair<TimeInterval, Pair<Integer, TrainsCycleItem>> pairI = i.next();
                // node found
                if (pairI.first.getOwner() == node)
                    break;
            }
            // check weight
            while (i.hasNext()) {
                Pair<TimeInterval, Pair<Integer, TrainsCycleItem>> pairI = i.next();
                if (pairI.first.isLineOwner()) {
                    if (retValue == null || retValue > pairI.second.first)
                        retValue = pairI.second.first;
                } else if (pairI.first.isNodeOwner()) {
                    retNode = pairI.first.getOwnerAsNode();
                    // next stop found
                    if (pairI.first.isStop() && retNode.getType() == NodeType.STATION_BRANCH)
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
     * @return next node and available weight
     */
    public static Pair<Node, Integer> getNextLength(Node node, Train train, TrainDiagram diagram) {
        Pair<Node, Integer> result = getNextWeight(node, train);
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
        Integer nodeLength = (Integer)node.getAttribute("length");
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
