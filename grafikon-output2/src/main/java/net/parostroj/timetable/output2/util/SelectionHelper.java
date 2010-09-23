package net.parostroj.timetable.output2.util;

import net.parostroj.timetable.output2.impl.RoutesExtractor;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.parostroj.timetable.actions.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.OutputParam;
import net.parostroj.timetable.output2.OutputParams;

/**
 * Helper class for selection.
 *
 * @author jub
 */
public class SelectionHelper {

    public static List<Route> getRoutes(OutputParams params, TrainDiagram diagram, List<Train> trains) {
        if (params.paramExistWithValue("routes")) {
            return (List<Route>) params.getParam("routes").getValue();
        } else {
            RoutesExtractor extractor = new RoutesExtractor(diagram);
            return extractor.getRoutesForTrains(trains);
        }
    }

    public static List<Train> selectTrains(OutputParams params, TrainDiagram diagram) {
        if (params.paramExistWithValue("trains")) {
            OutputParam param = params.getParam("trains");
            return (List<Train>) param.getValue();
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
            List<Route> routes = (List<Route>) params.getParam("routes").getValue();
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

    public static List<TrainsCycle> selectCycles(OutputParams params, TrainDiagram diagram, TrainsCycleType type) {
        OutputParam param = params.getParam("cycles");
        if (param != null && param.getValue() != null) {
            return (List<TrainsCycle>) param.getValue();
        }
        TrainsCycleSort s = new TrainsCycleSort(TrainsCycleSort.Type.ASC);
        return s.sort(diagram.getCycles(type));
    }

    public static List<Node> selectNodes(OutputParams params, TrainDiagram diagram) {
        OutputParam param = params.getParam("stations");
        if (param != null && param.getValue() != null) {
            return (List<Node>) param.getValue();
        }
        NodeSort s = new NodeSort(NodeSort.Type.ASC);
        return s.sort(diagram.getNet().getNodes(), new NodeFilter() {

            @Override
            public boolean check(Node node) {
                return node.getType().isStation() || node.getType().isStop();
            }
        });
    }
}
