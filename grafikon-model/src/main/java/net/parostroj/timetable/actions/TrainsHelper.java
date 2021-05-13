package net.parostroj.timetable.actions;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.LengthUnit;
import net.parostroj.timetable.model.units.UnitUtil;
import net.parostroj.timetable.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.*;

/**
 * Helper actions for trains.
 *
 * @author jub
 */
public final class TrainsHelper {

    private TrainsHelper() {}

    private static final Logger log = LoggerFactory.getLogger(TrainsHelper.class);

    public enum NextType {
        LAST_STATION, FIRST_STATION, BRANCH_STATION;
    }

    public static Integer getLength(TimeInterval interval) {
        Integer length = null;
        if (interval.isNodeOwner()) {
            Node node = interval.getOwnerAsNode();
            if (shouldCheckLength(node, interval.getTrain(), interval)) {
                length = node.getAttribute(Node.ATTR_LENGTH, Integer.class);
                length = convertLength(node.getDiagram(), length);
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
        Pair<Integer, Collection<TrainsCycleItem>> weightAndCycle = getWeightAndCycles(interval);
        if (weightAndCycle == null || weightAndCycle.first == null) {
            return null;
        } else {
            return weightAndCycle.first;
        }
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
    private static final Pattern NUMBER = Pattern.compile("\\d+");

    /**
     * return weight from weight attribute.
     *
     * @param train train
     * @return weight
     */
    public static Integer getWeightFromAttribute(Train train) {
        return train.getAttribute(Train.ATTR_WEIGHT, Integer.class);
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
        String weightStr = train.getAttribute("weight.info", String.class);
        // try to convert weight string to number
        if (weightStr != null) {
            try {
                Matcher matcher = NUMBER.matcher(weightStr);
                if (matcher.find()) {
                    String number = matcher.group(0);
                    weight = Integer.valueOf(number);
                }
            } catch (NumberFormatException e) {
                log.debug("Cannot convert weight to number: {}", weightStr);
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
    public static Pair<Integer, Collection<TrainsCycleItem>> getWeightAndCycles(TimeInterval interval) {
        if (!interval.isLineOwner()) {
            throw new IllegalArgumentException("Weight can be returned only for line interval.");
        }
        Pair<Integer, Collection<TrainsCycleItem>> retValue = null;
        LineClass lineClass = interval.getLineClass();
        Collection<TrainsCycleItem> items = getEngineCyclesForInterval(interval);
        if (lineClass != null) {
            // compute weight
            Integer weight = items.stream().reduce(null, (w, item) -> {
                EngineClass engine = getEngineClass(item);
                if (engine != null) {
                    WeightTableRow weightTableRow = engine.getWeightTableRowForSpeed(interval.getSpeed());
                    if (weightTableRow != null) {
                        w = weightTableRow.getWeight(lineClass) + (w == null ? 0 : w);
                    }
                }
                return w;
            }, (x, y) -> x + y);

            if (weight != null) {
                retValue = new Pair<>(weight, items);
            }
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
        Integer convertedLength = length;
        if (convertedLength == null)
            return null;
        LengthUnit lengthUnit = diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT, LengthUnit.class);
        if (lengthUnit == LengthUnit.AXLE) {
            Integer lpa = diagram.getAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE, Integer.class);
            Scale scale = diagram.getAttribute(TrainDiagram.ATTR_SCALE, Scale.class);
            convertedLength = (convertedLength * scale.getRatio()) / lpa;
        } else if (lengthUnit != null) {
            BigDecimal converted = lengthUnit.convertFrom(new BigDecimal(convertedLength), LengthUnit.MM);
            try {
                convertedLength = UnitUtil.convert(converted);
            } catch (ArithmeticException e) {
                log.warn("Couldn't convert value {} to {}: {}", convertedLength, lengthUnit.getKey(), e.getMessage());
            }
        }
        return convertedLength;
    }

    /**
     * converts weight to length based on conversion ratio and state of the train empty/loaded.
     *
     * @param train train
     * @param weight weight
     * @return length
     */
    public static Integer convertWeightToLength(Train train, Integer weight) {
        if (weight == null)
            return null;
        // weight in kg
        TrainDiagram diagram = train.getDiagram();
        Integer wpa = train.getAttributes().getBool(Train.ATTR_EMPTY) ?
            diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE_EMPTY, Integer.class) :
            diagram.getAttribute(TrainDiagram.ATTR_WEIGHT_PER_AXLE, Integer.class);
        LengthUnit lu = diagram.getAttribute(TrainDiagram.ATTR_LENGTH_UNIT, LengthUnit.class);
        // length in mm
        double axles = (double) (weight * 1000) / wpa;
        Integer result = null;
        if (lu == LengthUnit.AXLE) {
            // number of axles should be an even number
            result = (int) axles;
        } else {
            Integer lpa = diagram.getAttribute(TrainDiagram.ATTR_LENGTH_PER_AXLE, Integer.class);
            Scale scale = diagram.getAttribute(TrainDiagram.ATTR_SCALE, Scale.class);
            // length in mm
            result = (int)(axles * lpa);
            // adjust by scale
            result = result / scale.getRatio();
            // convert to unit
            BigDecimal converted = lu.convertFrom(new BigDecimal(result), LengthUnit.MM);
            try {
                result = UnitUtil.convert(converted);
            } catch (ArithmeticException e) {
                log.warn("Couldn't convert value {} to {}: {}", result, lu.getKey(), e.getMessage());
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
    public static Collection<TrainsCycleItem> getEngineCyclesForInterval(TimeInterval interval) {
        Train train = interval.getTrain();
        return train.getCycleItemsForInterval(train.getDiagram().getEngineCycleType(), interval);
    }

    /**
     * @param item circulation item to be checked
     * @return if the circulation item for engine is helper or not
     */
    public static boolean isHelperEngine(TrainsCycleItem item) {
        TrainsCycleType engineType = item.getTrain().getDiagram().getEngineCycleType();
        if (item.getCycle().getType() != engineType) {
            throw new IllegalArgumentException("Engine cycle expected.");
        }
        List<TimeInterval> intervals = item.getIntervals();
        int length = intervals.size();
        for (TimeInterval interval : intervals) {
            // check only line intervals (first and last node intervals overlap and it is enough
            // to check line intervals)
            if (interval.isNodeOwner()) {
                continue;
            }
            Collection<TrainsCycleItem> items = interval.getTrain().getCycleItemsForInterval(engineType, interval);
            for (TrainsCycleItem i : items) {
                if (i != item && length < i.getIntervals().size()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * returns list of engine classes for time interval.
     *
     * @param interval time interval
     * @return list of engine classes
     */
    public static List<EngineClass> getEngineClasses(TimeInterval interval) {
        Collection<TrainsCycleItem> list = getEngineCyclesForInterval(interval);
        return getEngineClasses(list);
    }

    /**
     * returns speed which is able to pull the weight.
     *
     * @param engineClasses engine classes
     * @param weight weight to be pulled
     * @return speed
     */
    public static Integer getSpeedForWeight(List<EngineClass> engineClasses, LineClass lineClass, int weight) {
        if (engineClasses.isEmpty() || lineClass == null) {
            return null;
        }
        TreeSet<Integer> speeds = engineClasses.stream()
                .flatMap(engineClass -> engineClass.getWeightTable().stream())
                .map(WeightTableRow::getSpeed)
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.<Integer>naturalOrder().reversed())));

        Integer result = null;
        for (Integer speed : speeds) {
            result = speed;
            int totalWeight = engineClasses.stream()
                    .map(ec -> ec.getWeightTableRowForSpeed(speed))
                    .reduce(0, (w, rfs) -> {
                        Integer ew = rfs != null ? rfs.getWeight(lineClass) : null;
                        return ew == null ? w : w + ew;
                    }, (w1, w2) -> w1 + w2);

            if (totalWeight >= weight) {
                break;
            }
        }
        return result;
    }

    /**
     * returns list of extracted engine classes from list of cycle items.
     *
     * @param list list of cycle items
     * @return list of engines
     */
    public static List<EngineClass> getEngineClasses(Collection<TrainsCycleItem> list) {
        ResultList<EngineClass> result = new ResultList<>();
        for (TrainsCycleItem item : list) {
            EngineClass eClass = getEngineClass(item);
            if (eClass != null) {
                result.add(eClass);
            }
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
        return item.getCycle().getAttribute(TrainsCycle.ATTR_ENGINE_CLASS, EngineClass.class);
    }

    /**
     * return list with weights for the train.
     *
     * @param train train
     * @return list with weights
     */
    public static List<Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>>> getWeightList(Train train) {
        List<TimeInterval> intervals = train.getTimeIntervalList();
        List<Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>>> result =
            new ArrayList<>(intervals.size());
        for (TimeInterval interval : intervals) {
            if (interval.isNodeOwner()) {
                result.add(new Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>>(interval, null, null));
            } else {
                Integer weight = getWeight(interval);
                Collection<TrainsCycleItem> cycles = getEngineCyclesForInterval(interval);
                result.add(new Triplet<>(interval, weight, cycles));
            }
        }
        return result;
    }

    /**
     * returns list of lengths.
     *
     * @param train train
     * @return list of lengths
     */
    public static List<Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>>> getLengthList(Train train) {
        List<TimeInterval> intervals = train.getTimeIntervalList();
        List<Triplet<TimeInterval, Integer, Collection<TrainsCycleItem>>> result =
            new ArrayList<>(intervals.size());
        for (TimeInterval interval : intervals) {
            Integer length = getLength(interval);
            Collection<TrainsCycleItem> cycles = interval.isLineOwner() ? getEngineCyclesForInterval(interval) : null;
            result.add(new Triplet<>(interval, length, cycles));
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
     * @param nextType type
     * @return next node and available weight
     */
    public static Pair<Node, Integer> getNextLength(Node node, Train train, NextType nextType) {
        Node endNode = getNextNodeByType(node, train, nextType);
        Integer length = getLengthFromTo(node, endNode, train);
        return length == null ? null : new Pair<>(endNode, length);
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
        PeekingIterator<TimeInterval> iterator = Iterators.peekingIterator(train.getTimeIntervalList().iterator());
        CollectionUtils.advanceTo(iterator, interval -> interval.getOwner() == from);
        while (iterator.hasNext()) {
            TimeInterval interval = iterator.next();
            Integer tempLength = getLength(interval);
            if (length == null || (tempLength != null && tempLength < length)) {
                length = tempLength;
            }
            if (interval.getOwner() == to) {
                break;
            }
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
        if (interval == null || !interval.isNodeOwner()) {
            throw new IllegalArgumentException("Wrong interval parameter: " + interval);
        }
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
        Iterator<TimeInterval> nodeIterator = train.getNodeIntervals().iterator();
        // advance to specified node
        Iterators.find(nodeIterator, item -> item.getOwner() == node, null);
        TimeInterval interval = Iterators.find(nodeIterator, item -> isNextTypeNode(item, type), null);
        return interval == null ? null : interval.getOwnerAsNode();
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
        boolean ignore = interval.getAttributes().getBool(TimeInterval.ATTR_IGNORE_LENGTH);
        TrainType type = train.getType();
        return (node.getType().isStation() || (node.getType().isStop() && (type != null && type.isPlatform()))) && !ignore;
    }

    /**
     * Returns sorted list by time of trains which goes through given route segment.
     *
     * @param trains trains to be filtered
     * @param segment route segment
     * @return iterable with trains
     */
    public static Iterable<Train> filterAndSortByNode(Iterable<Train> trains, final NetSegment<?> segment) {
        Iterable<TimeInterval> intervals = Iterables.filter(Iterables.transform(trains, train -> Iterables.<TimeInterval>find(train.getTimeIntervalList(),
                interval -> segment.equals(interval.getOwner()), null)), Predicates.notNull());
        Ordering<TimeInterval> sort = Ordering.from((TimeInterval i1, TimeInterval i2) -> i1.getStart() - i2.getStart());
        return Iterables.transform(sort.sortedCopy(intervals), TimeInterval::getTrain);
    }
}
