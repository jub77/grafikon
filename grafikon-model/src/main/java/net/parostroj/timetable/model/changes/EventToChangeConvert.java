package net.parostroj.timetable.model.changes;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainEvent;

/**
 * Converts types and other things from GTEventType to DiagramChange.
 *
 * @author jub
 */
class EventToChangeConvert {

    private static Map<GTEventType, DiagramChange.Type> TYPES;
    private static Map<GTEventType, DiagramChange.Action> ACTIONS;
    private static Map<GTEventType, String> DESCS;
    private static Map<TrainEvent.TimeIntervalListType, String> TIL_DESCS;

    static {
        TYPES = Collections.unmodifiableMap(getTypes());
        ACTIONS = Collections.unmodifiableMap(getActions());
        DESCS = Collections.unmodifiableMap(getDescs());
        TIL_DESCS = Collections.unmodifiableMap(getTilDescs());
    }

    private static Map<GTEventType, DiagramChange.Type> getTypes() {
        Map<GTEventType, DiagramChange.Type> map = new EnumMap<GTEventType, DiagramChange.Type>(GTEventType.class);
        map.put(GTEventType.ENGINE_CLASS_ADDED, DiagramChange.Type.ENGINE_CLASS);
        map.put(GTEventType.ENGINE_CLASS_REMOVED, DiagramChange.Type.ENGINE_CLASS);
        map.put(GTEventType.IMAGE_ADDED, DiagramChange.Type.IMAGE);
        map.put(GTEventType.IMAGE_REMOVED, DiagramChange.Type.IMAGE);
        map.put(GTEventType.LINE_ADDED, DiagramChange.Type.LINE);
        map.put(GTEventType.LINE_CLASS_ADDED, DiagramChange.Type.LINE_CLASS);
        map.put(GTEventType.LINE_CLASS_REMOVED, DiagramChange.Type.LINE_CLASS);
        map.put(GTEventType.LINE_REMOVED, DiagramChange.Type.LINE);
        map.put(GTEventType.NODE_ADDED, DiagramChange.Type.NODE);
        map.put(GTEventType.NODE_REMOVED, DiagramChange.Type.NODE);
        map.put(GTEventType.ROUTE_ADDED, DiagramChange.Type.ROUTE);
        map.put(GTEventType.ROUTE_REMOVED, DiagramChange.Type.ROUTE);
        map.put(GTEventType.TEXT_ITEM_ADDED, DiagramChange.Type.TEXT_ITEM);
        map.put(GTEventType.TEXT_ITEM_REMOVED, DiagramChange.Type.TEXT_ITEM);
        map.put(GTEventType.TRAINS_CYCLE_ADDED, DiagramChange.Type.TRAINS_CYCLE);
        map.put(GTEventType.TRAINS_CYCLE_REMOVED, DiagramChange.Type.TRAINS_CYCLE);
        map.put(GTEventType.CYCLE_TYPE_ADDED, DiagramChange.Type.CYCLE_TYPE);
        map.put(GTEventType.CYCLE_TYPE_REMOVED, DiagramChange.Type.CYCLE_TYPE);
        map.put(GTEventType.TRAIN_ADDED, DiagramChange.Type.TRAIN);
        map.put(GTEventType.TRAIN_REMOVED, DiagramChange.Type.TRAIN);
        map.put(GTEventType.TRAIN_TYPE_ADDED, DiagramChange.Type.TRAIN_TYPE);
        map.put(GTEventType.TRAIN_TYPE_REMOVED, DiagramChange.Type.TRAIN_TYPE);
        map.put(GTEventType.OUTPUT_TEMPLATE_ADDED, DiagramChange.Type.OUTPUT_TEMPLATE);
        map.put(GTEventType.OUTPUT_TEMPLATE_REMOVED, DiagramChange.Type.OUTPUT_TEMPLATE);
        map.put(GTEventType.GROUP_ADDED, DiagramChange.Type.GROUP);
        map.put(GTEventType.GROUP_REMOVED, DiagramChange.Type.GROUP);
        map.put(GTEventType.FREIGHT_NET_TRAIN_ADDED, DiagramChange.Type.FREIGHT_NET);
        map.put(GTEventType.FREIGHT_NET_TRAIN_REMOVED, DiagramChange.Type.FREIGHT_NET);
        map.put(GTEventType.FREIGHT_NET_CONNECTION_ADDED, DiagramChange.Type.FREIGHT_NET);
        map.put(GTEventType.FREIGHT_NET_CONNECTION_REMOVED, DiagramChange.Type.FREIGHT_NET);
        return map;
    }

    private static Map<GTEventType, DiagramChange.Action> getActions() {
        Map<GTEventType, DiagramChange.Action> map = new EnumMap<GTEventType, DiagramChange.Action>(GTEventType.class);
        map.put(GTEventType.ATTRIBUTE, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.ENGINE_CLASS_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.ENGINE_CLASS_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.IMAGE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.IMAGE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.LINE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.LINE_CLASS_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.LINE_CLASS_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.LINE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.NODE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.NODE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.ROUTE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.ROUTE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.TECHNOLOGICAL, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.TEXT_ITEM_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TEXT_ITEM_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.TIME_INTERVAL_ATTRIBUTE, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.TIME_INTERVAL_LIST, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.TRACK_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TRACK_ATTRIBUTE, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.TRACK_REMOVED, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.TRAINS_CYCLE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TRAINS_CYCLE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.CYCLE_TYPE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.CYCLE_TYPE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.TRAIN_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TRAIN_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.TRAIN_TYPE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TRAIN_TYPE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.WEIGHT_TABLE_MODIFIED, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.OUTPUT_TEMPLATE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.OUTPUT_TEMPLATE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.GROUP_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.GROUP_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.OUTPUT_TEMPLATE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.OUTPUT_TEMPLATE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.FREIGHT_NET_TRAIN_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.FREIGHT_NET_TRAIN_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.FREIGHT_NET_CONNECTION_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.FREIGHT_NET_CONNECTION_REMOVED, DiagramChange.Action.REMOVED);
        return map;
    }

    private static Map<GTEventType, String> getDescs() {
        Map<GTEventType, String> map = new EnumMap<GTEventType, String>(GTEventType.class);
        map.put(GTEventType.ATTRIBUTE, "attribute");
        map.put(GTEventType.CYCLE_ITEM_ADDED, "item_added");
        map.put(GTEventType.CYCLE_ITEM_MOVED, "item_moved");
        map.put(GTEventType.CYCLE_ITEM_REMOVED, "item_removed");
        map.put(GTEventType.CYCLE_ITEM_UPDATED, "item_updated");
        map.put(GTEventType.ENGINE_CLASS_ADDED, null); // nothing
        map.put(GTEventType.ENGINE_CLASS_MOVED, null); // nothing
        map.put(GTEventType.ENGINE_CLASS_REMOVED, null); // nothing
        map.put(GTEventType.IMAGE_ADDED, null); // nothing
        map.put(GTEventType.IMAGE_REMOVED, null); // nothing
        map.put(GTEventType.LINE_ADDED, null); // nothing
        map.put(GTEventType.LINE_CLASS_ADDED, null); // nothing
        map.put(GTEventType.LINE_CLASS_MOVED, null); // nothing
        map.put(GTEventType.LINE_CLASS_REMOVED, null); // nothing
        map.put(GTEventType.LINE_REMOVED, null); // nothing
        map.put(GTEventType.NODE_ADDED, null); // nothing
        map.put(GTEventType.NODE_REMOVED, null); // nothing
        map.put(GTEventType.ROUTE_ADDED, null); // nothing
        map.put(GTEventType.ROUTE_REMOVED, null); // nothing
        map.put(GTEventType.TECHNOLOGICAL, "technological");
        map.put(GTEventType.TEXT_ITEM_ADDED, null); // nothing
        map.put(GTEventType.TEXT_ITEM_MOVED, null); // nothing
        map.put(GTEventType.TEXT_ITEM_REMOVED, null); // nothing
        map.put(GTEventType.TIME_INTERVAL_ADDED, null); // nothing (segment)
        map.put(GTEventType.TIME_INTERVAL_ATTRIBUTE, "interval_attribute");
        map.put(GTEventType.TIME_INTERVAL_LIST, null); // specified by list type
        map.put(GTEventType.TIME_INTERVAL_REMOVED, null); // nothing (segment)
        map.put(GTEventType.TIME_INTERVAL_UPDATED, null); // nothing (segment)
        map.put(GTEventType.TRACK_ADDED, null); // nothing
        map.put(GTEventType.TRACK_ATTRIBUTE, "track_attribute");
        map.put(GTEventType.TRACK_MOVED, null); // nothing
        map.put(GTEventType.TRACK_REMOVED, null); // nothing
        map.put(GTEventType.TRAINS_CYCLE_ADDED, null); // nothing
        map.put(GTEventType.TRAINS_CYCLE_REMOVED, null); // nothing
        map.put(GTEventType.CYCLE_TYPE_ADDED, null);
        map.put(GTEventType.CYCLE_TYPE_REMOVED, null);
        map.put(GTEventType.TRAIN_ADDED, null); // nothing
        map.put(GTEventType.TRAIN_REMOVED, null); // nothing
        map.put(GTEventType.TRAIN_TYPE_ADDED, null); // nothing
        map.put(GTEventType.TRAIN_TYPE_MOVED, null); // nothing
        map.put(GTEventType.TRAIN_TYPE_REMOVED, null); // nothing
        map.put(GTEventType.WEIGHT_TABLE_MODIFIED, "weight_table");
        map.put(GTEventType.OUTPUT_TEMPLATE_ADDED, null); // nothing
        map.put(GTEventType.OUTPUT_TEMPLATE_MOVED, null); // nothing
        map.put(GTEventType.OUTPUT_TEMPLATE_REMOVED, null); // nothing
        map.put(GTEventType.GROUP_ADDED, null); // nothing
        map.put(GTEventType.GROUP_REMOVED, null); // nothing
        map.put(GTEventType.FREIGHT_NET_TRAIN_ADDED, null); // nothing
        map.put(GTEventType.FREIGHT_NET_TRAIN_REMOVED, null); // nothing
        map.put(GTEventType.FREIGHT_NET_CONNECTION_ADDED, null); // nothing
        map.put(GTEventType.FREIGHT_NET_CONNECTION_REMOVED, null); // nothing
        return map;
    }

    private static Map<TrainEvent.TimeIntervalListType, String> getTilDescs() {
        Map<TrainEvent.TimeIntervalListType, String> map = new EnumMap<TrainEvent.TimeIntervalListType, String>(TrainEvent.TimeIntervalListType.class);
        map.put(TrainEvent.TimeIntervalListType.ADDED, null); // nothing
        map.put(TrainEvent.TimeIntervalListType.MOVED, "train_moved");
        map.put(TrainEvent.TimeIntervalListType.RECALCULATE, "train_recalculated");
        map.put(TrainEvent.TimeIntervalListType.SPEED, "train_speed");
        map.put(TrainEvent.TimeIntervalListType.STOP_TIME, "train_stop_time");
        map.put(TrainEvent.TimeIntervalListType.TRACK, "train_track");
        return map;
    }

    public DiagramChange.Type getType(GTEventType eventType) {
        return TYPES.get(eventType);
    }

    public DiagramChange.Action getAction(GTEventType eventType) {
        return ACTIONS.get(eventType);
    }

    public String getDesc(GTEventType eventType) {
        return DESCS.get(eventType);
    }

    public String getTilDesc(TrainEvent.TimeIntervalListType type) {
        return TIL_DESCS.get(type);
    }
}

/* ALL TYPES
    map.put(GTEventType.ATTRIBUTE, null);
    map.put(GTEventType.CYCLE_ITEM_ADDED, null);
    map.put(GTEventType.CYCLE_ITEM_MOVED, null);
    map.put(GTEventType.CYCLE_ITEM_REMOVED, null);
    map.put(GTEventType.CYCLE_ITEM_UPDATED, null);
    map.put(GTEventType.ENGINE_CLASS_ADDED, null);
    map.put(GTEventType.ENGINE_CLASS_MOVED, null);
    map.put(GTEventType.ENGINE_CLASS_REMOVED, null);
    map.put(GTEventType.IMAGE_ADDED, null);
    map.put(GTEventType.IMAGE_REMOVED, null);
    map.put(GTEventType.LINE_ADDED, null);
    map.put(GTEventType.LINE_CLASS_ADDED, null);
    map.put(GTEventType.LINE_CLASS_MOVED, null);
    map.put(GTEventType.LINE_CLASS_REMOVED, null);
    map.put(GTEventType.LINE_REMOVED, null);
    map.put(GTEventType.NODE_ADDED, null);
    map.put(GTEventType.NODE_REMOVED, null);
    map.put(GTEventType.ROUTE_ADDED, null);
    map.put(GTEventType.ROUTE_REMOVED, null);
    map.put(GTEventType.TECHNOLOGICAL, null);
    map.put(GTEventType.TEXT_ITEM_ADDED, null);
    map.put(GTEventType.TEXT_ITEM_MOVED, null);
    map.put(GTEventType.TEXT_ITEM_REMOVED, null);
    map.put(GTEventType.TIME_INTERVAL_ADDED, null);
    map.put(GTEventType.TIME_INTERVAL_ATTRIBUTE, null);
    map.put(GTEventType.TIME_INTERVAL_LIST, null);
    map.put(GTEventType.TIME_INTERVAL_REMOVED, null);
    map.put(GTEventType.TIME_INTERVAL_UPDATED, null);
    map.put(GTEventType.TRACK_ADDED, null);
    map.put(GTEventType.TRACK_ATTRIBUTE, null);
    map.put(GTEventType.TRACK_MOVED, null);
    map.put(GTEventType.TRACK_REMOVED, null);
    map.put(GTEventType.TRAINS_CYCLE_ADDED, null);
    map.put(GTEventType.TRAINS_CYCLE_REMOVED, null);
    map.put(GTEventType.CYCLE_TYPE_ADDED, null);
    map.put(GTEventType.CYCLE_TYPE_REMOVED, null);
    map.put(GTEventType.TRAIN_ADDED, null);
    map.put(GTEventType.TRAIN_REMOVED, null);
    map.put(GTEventType.TRAIN_TYPE_ADDED, null);
    map.put(GTEventType.TRAIN_TYPE_MOVED, null);
    map.put(GTEventType.TRAIN_TYPE_REMOVED, null);
    map.put(GTEventType.WEIGHT_TABLE_MODIFIED, null);
    map.put(GTEventType.OUTPUT_TEMPLATE_ADDED, null);
    map.put(GTEventType.OUTPUT_TEMPLATE_MOVED, null);
    map.put(GTEventType.OUTPUT_TEMPLATE_REMOVED, null);
    map.put(GTEventType.GROUP_ADDED, null);
    map.put(GTEventType.GROUP_REMOVED, null);
    map.put(GTEventType.FREIGH_NET_TRAIN_ADDED, null); // nothing
    map.put(GTEventType.FREIGH_NET_TRAIN_REMOVED, null); // nothing
    map.put(GTEventType.FREIGH_NET_CONNECTION_ADDED, null); // nothing
    map.put(GTEventType.FREIGH_NET_CONNECTION_REMOVED, null); // nothing
 */