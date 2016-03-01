package net.parostroj.timetable.model.events;

/**
 * Event type.
 *
 * @author jub
 */
public enum GTEventType {
    ATTRIBUTE, CYCLE_ITEM_ADDED, CYCLE_ITEM_REMOVED, CYCLE_ITEM_UPDATED, CYCLE_ITEM_MOVED,
    CYCLE_SEQUENCE, TRACK_ADDED, TRACK_REMOVED, TRACK_MOVED, TIME_INTERVAL_ATTRIBUTE,
    TIME_INTERVAL_ADDED, TIME_INTERVAL_REMOVED, TIME_INTERVAL_UPDATED, TRACK_ATTRIBUTE,
    NODE_ADDED, NODE_REMOVED, LINE_ADDED, LINE_REMOVED,
    LINE_CLASS_ADDED, LINE_CLASS_REMOVED, LINE_CLASS_MOVED,
    ROUTE_ADDED, ROUTE_REMOVED, TRAIN_ADDED, TRAIN_REMOVED, TRAIN_TYPE_ADDED, TRAIN_TYPE_REMOVED,
    TRAIN_TYPE_MOVED, ENGINE_CLASS_ADDED, ENGINE_CLASS_REMOVED, ENGINE_CLASS_MOVED,
    WEIGHT_TABLE_MODIFIED, TIME_INTERVAL_LIST, TECHNOLOGICAL,
    TEXT_ITEM_ADDED, TEXT_ITEM_REMOVED, TEXT_ITEM_MOVED,
    IMAGE_ADDED, IMAGE_REMOVED, TRAINS_CYCLE_ADDED, TRAINS_CYCLE_REMOVED,
    OUTPUT_TEMPLATE_ADDED, OUTPUT_TEMPLATE_REMOVED, OUTPUT_TEMPLATE_MOVED,
    CYCLE_TYPE_ADDED, CYCLE_TYPE_REMOVED, GROUP_ADDED, GROUP_REMOVED,
    FREIGHT_NET_CONNECTION_ADDED, FREIGHT_NET_CONNECTION_REMOVED, FREIGHT_NET_CONNECTION_ATTRIBUTE,
    REGION_ADDED, REGION_REMOVED, REGION_MOVED, COMPANY_ADDED, COMPANY_REMOVED, OBJECT_ATTRIBUTE;
}