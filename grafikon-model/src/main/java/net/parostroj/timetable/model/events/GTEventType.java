package net.parostroj.timetable.model.events;

/**
 * Event type.
 *
 * @author jub
 */
public enum GTEventType {
    ATTRIBUTE, CYCLE_ITEM_ADDED, CYCLE_ITEM_REMOVED, CYCLE_ITEM_UPDATED,
    TRACK_ADDED, TRACK_REMOVED, TRACK_MOVED,
    TIME_INTERVAL_ADDED, TIME_INTERVAL_REMOVED, TIME_INTERVAL_UPDATED, TRACK_ATTRIBUTE,
    NODE_ADDED, NODE_REMOVED, LINE_ADDED, LINE_REMOVED,
    LINE_CLASS_ADDED, LINE_CLASS_REMOVED, LINE_CLASS_MOVED, NESTED,
    ROUTE_ADDED, ROUTE_REMOVED, TRAIN_ADDED, TRAIN_REMOVED, TRAIN_TYPE_ADDED, TRAIN_TYPE_REMOVED,
    TIME_INTERVAL_LIST, TECHNOLOGICAL;
}
