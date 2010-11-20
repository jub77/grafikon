package net.parostroj.timetable.utils;

import java.util.List;
import java.util.ListIterator;
import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;

/**
 * Transformation of texts.
 * 
 * @author jub
 */
public class TransformUtil {
    /**
     * creates name of the station.
     * 
     * @param node station
     * @param stop abbreviation for stops
     * @param stopFreight abbreviation for stops with freight
     * @return transformed name
     */
    public static String transformStation(Node node, String stop, String stopFreight) {
        String name = node.getName();
        if (node.getType() == NodeType.STOP && stop != null)
            name += " " + stop;
        else if (node.getType() == NodeType.STOP_WITH_FREIGHT && stopFreight != null)
            name += " " + stopFreight;
        return name;
    }
    
    public static String getFromAbbr(TimeInterval i) {
        Node node = null;
        boolean found = false;
        for (TimeInterval current : i.getTrain().getTimeIntervalList()) {
            if (current == i) {
                found = true;
                break;
            }
            if (current.getOwner() instanceof Node) {
                Node n = (Node)current.getOwner();
                switch (n.getType()) {
                    case STOP: case STOP_WITH_FREIGHT: case ROUTE_SPLIT: case SIGNAL:
                        // do nothing
                        break;
                    default:
                        node = n;
                        break;
                }
            }
        }
        if (node != null && found)
            return node.getAbbr();
        else
            return null;
    }
    
    public static String getToAbbr(TimeInterval i) {
        Node node = null;
        boolean found = false;
        List<TimeInterval> list = i.getTrain().getTimeIntervalList();
        ListIterator<TimeInterval> iterator = list.listIterator(list.size());
        while (iterator.hasPrevious()) {
            TimeInterval current = iterator.previous();
            if (current == i) {
                found = true;
                break;
            }
            if (current.getOwner() instanceof Node) {
                Node n = (Node)current.getOwner();
                switch (n.getType()) {
                    case STOP: case STOP_WITH_FREIGHT: case ROUTE_SPLIT: case SIGNAL:
                        // do nothing
                        break;
                    default:
                        node = n;
                        break;
                }
            }
        }
        if (found && node != null)
            return node.getAbbr();
        else
            return null;
    }
    
    public static String getEngineCycleDescription(TrainsCycle ec) {
        if (ec.getType() != TrainsCycleType.ENGINE_CYCLE)
            throw new IllegalArgumentException("Engine cycle expected.");
        
        String result = (ec.getDescription() != null) ? ec.getDescription().trim() : "";
        if (ec.getAttribute("engine.class") != null) {
            EngineClass cl = (EngineClass)ec.getAttribute("engine.class");
            String desc = result;
            result = cl.getName();
            if (!"".equals(desc)) {
                result = String.format("%s (%s)", result, desc);
            }
        }
        return result;
    }

    public static String getEngineDescription(TrainsCycle ec) {
        EngineClass cl = (EngineClass)ec.getAttribute("engine.class");
        if (cl != null)
            return cl.getName();
        else {
            return ec.getDescription();
        }
    }
}
