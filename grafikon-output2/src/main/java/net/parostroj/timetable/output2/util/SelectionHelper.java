package net.parostroj.timetable.output2.util;

import java.util.*;

import net.parostroj.timetable.actions.*;
import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.OutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.impl.RoutesExtractor;

/**
 * Helper class for selection.
 *
 * @author jub
 */
public class SelectionHelper {

    public static List<Route> getRoutes(OutputParams params, TrainDiagram diagram, List<Train> trains) {
        if (params.paramExistWithValue("routes")) {
            return getList((List<?>) params.getParam("routes").getValue(), Route.class);
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

    private static <T> List<T> getList(List<?> orig, Class<T> clazz) {
        List<T> dest = new LinkedList<T>();
        for (Object o : orig) {
            dest.add(clazz.cast(o));
        }
        return dest;
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
            return getList((List<?>) param.getValue(), Train.class);
        } else if (params.paramExistWithValue("station")) {
            Node station = (Node) params.getParam("station").getValue();
            return (new TrainSortByNodeFilter()).sortAndFilter(diagram.getTrains(), station);
        } else if (params.paramExistWithValue("driver_cycle")) {
            TrainsCycle cycle = (TrainsCycle) params.getParam("driver_cycle").getValue();
            List<Train> trains = new LinkedList<Train>();
            for (TrainsCycleItem item : cycle) {
                trains.add(item.getTrain());
            }
            return trains;
        } else if (params.paramExistWithValue("routes")) {
            List<Route> routes = getList((List<?>) params.getParam("routes").getValue(), Route.class);
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
            TrainSort s = new TrainSort(
                    new TrainComparator(TrainComparator.Type.ASC,
                    diagram.getTrainsData().getTrainSortPattern()));
            return s.sort(trains);
        } else {
            TrainSort s = new TrainSort(
                    new TrainComparator(TrainComparator.Type.ASC,
                    diagram.getTrainsData().getTrainSortPattern()));
            return s.sort(diagram.getTrains());
        }
    }

    public static List<TrainsCycle> selectCycles(OutputParams params, TrainDiagram diagram, String type) {
        OutputParam param = params.getParam("cycles");
        if (param != null && param.getValue() != null) {
            return getList((List<?>) param.getValue(), TrainsCycle.class);
        }
        TrainsCycleSort s = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        return s.sort(getCycleByType(diagram, type));
    }

    public static List<Node> selectNodes(OutputParams params, TrainDiagram diagram) {
        OutputParam param = params.getParam("stations");
        if (param != null && param.getValue() != null) {
            return getList((List<?>) param.getValue(), Node.class);
        }
        NodeSort s = new NodeSort(NodeSort.Type.ASC);
        return s.sort(diagram.getNet().getNodes(), new Filter<Node>() {

            @Override
            public boolean is(Node node) {
                return node.getType().isStation() || node.getType().isStop();
            }
        });
    }

    private static List<TrainsCycle> getCycleByType(TrainDiagram diagram, String type) {
        if (type != null) {
            return diagram.getCycles(type);
        } else {
            // collect all non-default
            List<TrainsCycle> result = new LinkedList<TrainsCycle>();
            for (String aType : diagram.getCycleTypeNames()) {
                if (!TrainsCycleType.isDefaultType(aType))
                    result.addAll(diagram.getCycles(aType));
            }
            return result;
        }
    }
}
