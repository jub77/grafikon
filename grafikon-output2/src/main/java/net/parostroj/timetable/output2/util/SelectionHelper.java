package net.parostroj.timetable.output2.util;

import java.util.*;

import com.google.common.collect.Lists;

import net.parostroj.timetable.actions.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.OutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.RoutesExtractor;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Helper class for selection.
 *
 * @author jub
 */
public class SelectionHelper {

    public static List<Route> getRoutes(OutputParams params, TrainDiagram diagram, List<Train> trains) {
        if (params.paramExistWithValue("routes")) {
            return ObjectsUtil.getList((List<?>) params.getParam("routes").getValue(), Route.class);
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
            return (TrainsCycle) params.getParam("driver_cycle").getValue();
        } else {
            return null;
        }
    }

    public static List<Train> selectTrains(OutputParams params, TrainDiagram diagram) {
        if (params.paramExistWithValue("trains")) {
            OutputParam param = params.getParam("trains");
            return ObjectsUtil.getList((List<?>) param.getValue(), Train.class);
        } else if (params.paramExistWithValue("station")) {
            Node station = (Node) params.getParam("station").getValue();
            return Lists.newArrayList(TrainsHelper.filterAndSortByNode(diagram.getTrains(), station));
        } else if (params.paramExistWithValue("driver_cycle")) {
            TrainsCycle cycle = (TrainsCycle) params.getParam("driver_cycle").getValue();
            List<Train> trains = new LinkedList<Train>();
            for (TrainsCycleItem item : cycle) {
                trains.add(item.getTrain());
            }
            return trains;
        } else if (params.paramExistWithValue("routes")) {
            List<Route> routes = ObjectsUtil.getList((List<?>) params.getParam("routes").getValue(), Route.class);
            Set<Train> trains = new HashSet<Train>();
            for (Route route : routes) {
                for (RouteSegment seg : route.getSegments()) {
                    if (seg.isLine()) {
                        for (Track track : seg.asLine().getTracks()) {
                            for (TimeInterval i : track.getTimeIntervalList()) {
                                trains.add(i.getTrain());
                            }
                        }
                    }
                }
            }
            ElementSort<Train> s = new ElementSort<Train>(
                    new TrainComparator(diagram.getTrainsData().getTrainSortPattern()));
            return s.sort(trains);
        } else {
            ElementSort<Train> s = new ElementSort<Train>(
                    new TrainComparator(diagram.getTrainsData().getTrainSortPattern()));
            return s.sort(diagram.getTrains());
        }
    }

    public static List<TrainsCycle> selectCycles(OutputParams params, TrainDiagram diagram, TrainsCycleType type) {
        OutputParam param = params.getParam("cycles");
        if (param != null && param.getValue() != null) {
            return ObjectsUtil.getList((List<?>) param.getValue(), TrainsCycle.class);
        }
        ElementSort<TrainsCycle> s = new ElementSort<TrainsCycle>(new TrainsCycleComparator());
        return s.sort(getCycleByType(diagram, type));
    }

    public static List<Node> selectNodes(OutputParams params, TrainDiagram diagram) {
        OutputParam param = params.getParam("stations");
        if (param != null && param.getValue() != null) {
            return ObjectsUtil.getList((List<?>) param.getValue(), Node.class);
        }
        ElementSort<Node> s = new ElementSort<Node>(new NodeComparator(),
                node -> node.getType().isStation() || node.getType().isStop());
        return s.sort(diagram.getNet().getNodes());
    }

    private static List<TrainsCycle> getCycleByType(TrainDiagram diagram, TrainsCycleType type) {
        if (type != null) {
            return diagram.getCycles(type);
        } else {
            // collect all non-default
            List<TrainsCycle> result = new LinkedList<TrainsCycle>();
            for (TrainsCycleType aType : diagram.getCycleTypes()) {
                if (!TrainsCycleType.isDefaultType(aType))
                    result.addAll(diagram.getCycles(aType));
            }
            return result;
        }
    }
}
