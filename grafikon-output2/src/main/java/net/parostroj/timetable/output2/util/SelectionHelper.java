package net.parostroj.timetable.output2.util;

import java.util.*;

import com.google.common.collect.Lists;

import net.parostroj.timetable.actions.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.RoutesExtractor;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Helper class for selection.
 *
 * @author jub
 */
public final class SelectionHelper {

    private SelectionHelper() {}

    public static List<Route> getRoutes(OutputParams params, TrainDiagram diagram, List<Train> trains) {
        if (params.paramExistWithValue("routes")) {
            return ObjectsUtil.copyToList(params.getParamValue("routes", Collection.class), Route.class);
        } else {
            RoutesExtractor extractor = new RoutesExtractor(diagram);
            TrainsCycle cycle = getDriverCycle(params);
            if (cycle != null) {
                Set<Line> lines = extractor.getLinesForCycle(cycle);
                return extractor.getRoutesForLines(lines);
            } else {
                return extractor.getRoutesForTrains(trains);
            }
        }
    }

    public static TrainsCycle getDriverCycle(OutputParams params) {
        if (params.paramExistWithValue("driver_cycle")) {
            return params.getParamValue("driver_cycle", TrainsCycle.class);
        } else {
            return null;
        }
    }

    public static List<Train> selectTrains(OutputParams params, TrainDiagram diagram) {
        if (params.paramExistWithValue("trains")) {
            Collection<?> trains = params.getParamValue("trains", Collection.class);
            ElementSort<Train> s = new ElementSort<>(
                    new TrainComparator(diagram.getTrainsData().getTrainSortPattern()));
            return s.sort(ObjectsUtil.copyToList(trains, Train.class));
        } else if (params.paramExistWithValue("station")) {
            Node station = params.getParamValue("station", Node.class);
            return Lists.newArrayList(TrainsHelper.filterAndSortByNode(diagram.getTrains(), station));
        } else if (params.paramExistWithValue("driver_cycle")) {
            TrainsCycle cycle = (TrainsCycle) params.getParam("driver_cycle").getValue();
            List<Train> trains = new LinkedList<>();
            for (TrainsCycleItem item : cycle) {
                trains.add(item.getTrain());
            }
            return trains;
        } else if (params.paramExistWithValue("routes")) {
            Collection<Route> routes = ObjectsUtil.copyToList(params.getParamValue("routes", Collection.class), Route.class);
            Set<Train> trains = new HashSet<>();
            for (Route route : routes) {
                for (Line line : route.getLines()) {
                    for (TimeInterval i : line) {
                        trains.add(i.getTrain());
                    }
                }
            }
            ElementSort<Train> s = new ElementSort<>(
                    new TrainComparator(diagram.getTrainsData().getTrainSortPattern()));
            return s.sort(trains);
        } else {
            ElementSort<Train> s = new ElementSort<>(
                    new TrainComparator(diagram.getTrainsData().getTrainSortPattern()));
            return s.sort(diagram.getTrains());
        }
    }

    public static List<TrainsCycle> selectCycles(OutputParams params, TrainDiagram diagram, TrainsCycleType type) {
        Collection<TrainsCycle> cycles = params.paramExistWithValue("cycles")
                ? ObjectsUtil.copyToList(params.getParamValue("cycles", Collection.class), TrainsCycle.class)
                : getCycleByType(diagram, type);
        ElementSort<TrainsCycle> s = new ElementSort<>(new TrainsCycleComparator());
        return s.sort(cycles);
    }

    public static List<Node> selectNodes(OutputParams params, TrainDiagram diagram) {
        Collection<Node> nodes = params.paramExistWithValue("stations")
                ? ObjectsUtil.copyToList(params.getParamValue("stations", Collection.class), Node.class)
                : diagram.getNet().getNodes();
        ElementSort<Node> s = new ElementSort<>(new NodeComparator(),
                node -> node.getType().isStation() || node.getType().isStop());
        return s.sort(nodes);
    }

    private static List<TrainsCycle> getCycleByType(TrainDiagram diagram, TrainsCycleType type) {
        if (type != null) {
            return new ArrayList<>(type.getCycles());
        } else {
            // collect all non-default
            List<TrainsCycle> result = new LinkedList<>();
            for (TrainsCycleType aType : diagram.getCycleTypes()) {
                if (!TrainsCycleType.isDefaultType(aType))
                    result.addAll(aType.getCycles());
            }
            return result;
        }
    }
}
