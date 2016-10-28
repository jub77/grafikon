package net.parostroj.timetable.gui;

import java.awt.Window;

/**
 * Context to pass common data between component.
 *
 * @author jub
 */
public interface GuiContext {

    default void registerWindow(String key, Window window) {
        registerWindow(key, window, null);
    }

    void registerWindow(String key, Window window, GuiContextDataListener listener);
}
