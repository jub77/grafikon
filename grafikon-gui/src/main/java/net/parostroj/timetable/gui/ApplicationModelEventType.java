/*
 * ApplicationModelEventType.java
 *
 * Created on 2.9.2007, 15:20:32
 */

package net.parostroj.timetable.gui;

/**
 * Event type for model.
 * 
 * @author jub
 */
public enum ApplicationModelEventType {
    SELECTED_TRAIN_CHANGED, SET_DIAGRAM_CHANGED, NEW_TRAIN, DELETE_TRAIN, MODIFIED_TRAIN, ROUTES_MODIFIED,
    SELECTED_ENGINE_CYCLE_CHANGED, NEW_ENGINE_CYCLE, DELETE_ENGINE_CYCLE, MODIFIED_ENGINE_CYCLE,
    SELECTED_TRAIN_UNIT_CYCLE_CHANGED, NEW_TRAIN_UNIT_CYCLE, DELETE_TRAIN_UNIT_CYCLE, MODIFIED_TRAIN_UNIT_CYCLE,
    SELECTED_DRIVER_CYCLE_CHANGED, NEW_DRIVER_CYCLE, DELETE_DRIVER_CYCLE, MODIFIED_DRIVER_CYCLE, MODIFIED_TRAIN_NAME_TYPE,
    MODIFIED_NODE, MODIFIED_LINE, MODEL_CHANGED, MODEL_SAVED, NEW_LINE, NEW_NODE, MODIFIED_TRAIN_ATTRIBUTE, TRAIN_TYPES_CHANGED,
    LINE_CLASSES_CHANGED, ENGINE_CLASSES_CHANGED, DELETE_NODE, DELETE_LINE, ADD_LAST_OPENED, REMOVE_LAST_OPENED;
}
