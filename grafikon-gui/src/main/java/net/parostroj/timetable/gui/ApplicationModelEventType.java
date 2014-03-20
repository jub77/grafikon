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
    SELECTED_TRAIN_CHANGED, SET_DIAGRAM_CHANGED, NEW_TRAIN, DELETE_TRAIN, MODIFIED_TRAIN, ROUTES_MODIFIED, MODIFIED_TRAIN_NAME_TYPE,
    MODIFIED_NODE, MODIFIED_LINE, MODEL_CHANGED, MODEL_SAVED, MODIFIED_TRAIN_ATTRIBUTE, TRAIN_TYPES_CHANGED,
    ADD_LAST_OPENED, REMOVE_LAST_OPENED;
}
