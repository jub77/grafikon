package net.parostroj.timetable.model.changes;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.parostroj.timetable.model.events.GTEventType;

/**
 * Converts types and other things from GTEventType to DiagramChange.
 *
 * @author jub
 */
class EventToChangeConvert {

    private static Map<GTEventType, DiagramChange.Type> TYPES;
    private static Map<GTEventType, DiagramChange.Action> ACTIONS;
    private static Map<GTEventType, String> DESCS;

    static {
        TYPES = Collections.unmodifiableMap(getTypes());
        ACTIONS = Collections.unmodifiableMap(getActions());
        DESCS = Collections.unmodifiableMap(getDescs());
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
        map.put(GTEventType.TRAIN_ADDED, DiagramChange.Type.TRAIN);
        map.put(GTEventType.TRAIN_REMOVED, DiagramChange.Type.TRAIN);
        map.put(GTEventType.TRAIN_TYPE_ADDED, DiagramChange.Type.TRAIN_TYPE);
        map.put(GTEventType.TRAIN_TYPE_REMOVED, DiagramChange.Type.TRAIN_TYPE);
        return map;
    }

    private static Map<GTEventType, DiagramChange.Action> getActions() {
        Map<GTEventType, DiagramChange.Action> map = new EnumMap<GTEventType, DiagramChange.Action>(GTEventType.class);
        map.put(GTEventType.ATTRIBUTE, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.CYCLE_ITEM_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.CYCLE_ITEM_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.CYCLE_ITEM_UPDATED, DiagramChange.Action.MODIFIED);
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
        map.put(GTEventType.TIME_INTERVAL_LIST, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.TRACK_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TRACK_ATTRIBUTE, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.TRACK_REMOVED, DiagramChange.Action.MODIFIED);
        map.put(GTEventType.TRAINS_CYCLE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TRAINS_CYCLE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.TRAIN_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TRAIN_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.TRAIN_TYPE_ADDED, DiagramChange.Action.ADDED);
        map.put(GTEventType.TRAIN_TYPE_REMOVED, DiagramChange.Action.REMOVED);
        map.put(GTEventType.WEIGHT_TABLE_MODIFIED, DiagramChange.Action.MODIFIED);
        return map;
    }

    private static Map<GTEventType, String> getDescs() {
        Map<GTEventType, String> map = new EnumMap<GTEventType, String>(GTEventType.class);
        map.put(GTEventType.ATTRIBUTE, "attribute_change");
        map.put(GTEventType.CYCLE_ITEM_ADDED, null);
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
        map.put(GTEventType.NESTED, null);
        map.put(GTEventType.NODE_ADDED, null);
        map.put(GTEventType.NODE_REMOVED, null);
        map.put(GTEventType.ROUTE_ADDED, null);
        map.put(GTEventType.ROUTE_REMOVED, null);
        map.put(GTEventType.TECHNOLOGICAL, null);
        map.put(GTEventType.TEXT_ITEM_ADDED, null);
        map.put(GTEventType.TEXT_ITEM_MOVED, null);
        map.put(GTEventType.TEXT_ITEM_REMOVED, null);
        map.put(GTEventType.TIME_INTERVAL_ADDED, null);
        map.put(GTEventType.TIME_INTERVAL_LIST, null);
        map.put(GTEventType.TIME_INTERVAL_REMOVED, null);
        map.put(GTEventType.TIME_INTERVAL_UPDATED, null);
        map.put(GTEventType.TRACK_ADDED, null);
        map.put(GTEventType.TRACK_ATTRIBUTE, null);
        map.put(GTEventType.TRACK_MOVED, null);
        map.put(GTEventType.TRACK_REMOVED, null);
        map.put(GTEventType.TRAINS_CYCLE_ADDED, null);
        map.put(GTEventType.TRAINS_CYCLE_REMOVED, null);
        map.put(GTEventType.TRAIN_ADDED, null);
        map.put(GTEventType.TRAIN_REMOVED, null);
        map.put(GTEventType.TRAIN_TYPE_ADDED, null);
        map.put(GTEventType.TRAIN_TYPE_MOVED, null);
        map.put(GTEventType.TRAIN_TYPE_REMOVED, null);
        map.put(GTEventType.WEIGHT_TABLE_MODIFIED, null);
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
}

/* ALL TYPES
    map.put(GTEventType.ATTRIBUTE, null);
    map.put(GTEventType.CYCLE_ITEM_ADDED, null);
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
    map.put(GTEventType.NESTED, null);
    map.put(GTEventType.NODE_ADDED, null);
    map.put(GTEventType.NODE_REMOVED, null);
    map.put(GTEventType.ROUTE_ADDED, null);
    map.put(GTEventType.ROUTE_REMOVED, null);
    map.put(GTEventType.TECHNOLOGICAL, null);
    map.put(GTEventType.TEXT_ITEM_ADDED, null);
    map.put(GTEventType.TEXT_ITEM_MOVED, null);
    map.put(GTEventType.TEXT_ITEM_REMOVED, null);
    map.put(GTEventType.TIME_INTERVAL_ADDED, null);
    map.put(GTEventType.TIME_INTERVAL_LIST, null);
    map.put(GTEventType.TIME_INTERVAL_REMOVED, null);
    map.put(GTEventType.TIME_INTERVAL_UPDATED, null);
    map.put(GTEventType.TRACK_ADDED, null);
    map.put(GTEventType.TRACK_ATTRIBUTE, null);
    map.put(GTEventType.TRACK_MOVED, null);
    map.put(GTEventType.TRACK_REMOVED, null);
    map.put(GTEventType.TRAINS_CYCLE_ADDED, null);
    map.put(GTEventType.TRAINS_CYCLE_REMOVED, null);
    map.put(GTEventType.TRAIN_ADDED, null);
    map.put(GTEventType.TRAIN_REMOVED, null);
    map.put(GTEventType.TRAIN_TYPE_ADDED, null);
    map.put(GTEventType.TRAIN_TYPE_MOVED, null);
    map.put(GTEventType.TRAIN_TYPE_REMOVED, null);
    map.put(GTEventType.WEIGHT_TABLE_MODIFIED, null);
 */