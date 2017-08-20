package net.parostroj.timetable.model.changes;

import java.util.*;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;

/**
 * Converts types and other things from GTEventType to DiagramChange.
 *
 * @author jub
 */
class EventToChangeConvert {

    private static Map<Class<?>, DiagramChange.Type> TYPES;
    private static Map<Event.Type, DiagramChange.Action> ACTIONS;
    private static Map<Event.Type, String> DESCS;
    private static Map<SpecialTrainTimeIntervalList.Type, String> TIL_DESCS;

    static {
        TYPES = Collections.unmodifiableMap(getTypes());
        ACTIONS = Collections.unmodifiableMap(getActions());
        DESCS = Collections.unmodifiableMap(getDescs());
        TIL_DESCS = Collections.unmodifiableMap(getTilDescs());
    }

    private static Map<Class<?>, DiagramChange.Type> getTypes() {
        Map<Class<?>, DiagramChange.Type> map = new HashMap<>();
        map.put(EngineClass.class, DiagramChange.Type.ENGINE_CLASS);
        map.put(TimetableImage.class, DiagramChange.Type.IMAGE);
        map.put(Line.class, DiagramChange.Type.LINE);
        map.put(Node.class, DiagramChange.Type.NODE);
        map.put(Route.class, DiagramChange.Type.ROUTE);
        map.put(TextItem.class, DiagramChange.Type.TEXT_ITEM);
        map.put(TrainsCycle.class, DiagramChange.Type.TRAINS_CYCLE);
        map.put(TrainsCycleType.class, DiagramChange.Type.CYCLE_TYPE);
        map.put(Train.class, DiagramChange.Type.TRAIN);
        map.put(TrainType.class, DiagramChange.Type.TRAIN_TYPE);
        map.put(OutputTemplate.class, DiagramChange.Type.OUTPUT_TEMPLATE);
        map.put(Group.class, DiagramChange.Type.GROUP);
        map.put(Company.class, DiagramChange.Type.COMPANY);
        map.put(FreightNet.class, DiagramChange.Type.FREIGHT_NET);
        map.put(Region.class, DiagramChange.Type.REGION);
        map.put(LineClass.class, DiagramChange.Type.LINE_CLASS);
        map.put(Net.class, DiagramChange.Type.NET);
        map.put(TrainDiagram.class, DiagramChange.Type.DIAGRAM);
        return map;
    }

    private static Map<Event.Type, DiagramChange.Action> getActions() {
        Map<Event.Type, DiagramChange.Action> map = new EnumMap<>(Event.Type.class);
        map.put(Event.Type.ATTRIBUTE, DiagramChange.Action.MODIFIED);
        map.put(Event.Type.OBJECT_ATTRIBUTE, DiagramChange.Action.MODIFIED);
        map.put(Event.Type.SPECIAL, DiagramChange.Action.MODIFIED);
        map.put(Event.Type.ADDED, DiagramChange.Action.ADDED);
        map.put(Event.Type.REMOVED, DiagramChange.Action.REMOVED);
        map.put(Event.Type.REPLACED, DiagramChange.Action.MODIFIED);
        map.put(Event.Type.MOVED, DiagramChange.Action.MOVED);
        return map;
    }

    private static Map<Event.Type, String> getDescs() {
        Map<Event.Type, String> map = new EnumMap<>(Event.Type.class);
        map.put(Event.Type.ATTRIBUTE, "attribute");
        map.put(Event.Type.OBJECT_ATTRIBUTE, "object_attribute");
        map.put(Event.Type.ADDED, "object_added");
        map.put(Event.Type.REMOVED, "object_removed");
        map.put(Event.Type.REPLACED, "object_replaced");
        map.put(Event.Type.MOVED, "object_moved");
        map.put(Event.Type.SPECIAL, "special");
        return map;
    }

    private static Map<SpecialTrainTimeIntervalList.Type, String> getTilDescs() {
        Map<SpecialTrainTimeIntervalList.Type, String> map = new EnumMap<>(
                SpecialTrainTimeIntervalList.Type.class);
        map.put(SpecialTrainTimeIntervalList.Type.ADDED, null); // nothing
        map.put(SpecialTrainTimeIntervalList.Type.MOVED, "train_moved");
        map.put(SpecialTrainTimeIntervalList.Type.RECALCULATE, "train_recalculated");
        map.put(SpecialTrainTimeIntervalList.Type.SPEED, "train_speed");
        map.put(SpecialTrainTimeIntervalList.Type.STOP_TIME, "train_stop_time");
        map.put(SpecialTrainTimeIntervalList.Type.TRACK, "train_track");
        return map;
    }

    public DiagramChange.Type getType(Object object) {
        return TYPES.get(object.getClass());
    }

    public DiagramChange.Action getAction(Event.Type eventType) {
        return ACTIONS.get(eventType);
    }

    public String getDesc(Event.Type eventType) {
        return DESCS.get(eventType);
    }

    public String getTilDesc(SpecialTrainTimeIntervalList.Type type) {
        return TIL_DESCS.get(type);
    }
}
