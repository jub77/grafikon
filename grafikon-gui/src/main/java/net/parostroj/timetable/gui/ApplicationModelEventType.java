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
    SELECTED_TRAIN_CHANGED, SET_DIAGRAM_CHANGED, MODIFIED_TRAIN, MODIFIED_TRAIN_NAME_TYPE,
    MODEL_CHANGED, MODEL_SAVED,
    ADD_LAST_OPENED, REMOVE_LAST_OPENED;
}
