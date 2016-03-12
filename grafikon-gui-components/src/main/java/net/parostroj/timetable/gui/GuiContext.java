package net.parostroj.timetable.gui;

import java.awt.Window;

/**
 * Context to pass common data between component.
 *
 * @author jub
 */
public interface GuiContext {

    void registerWindow(String key, Window window);
}
