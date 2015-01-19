package net.parostroj.timetable.gui.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;

/**
 * Stores location and size of the window.
 *
 * @author jub
 */
public class WindowLocationSize {

    private Point location;
    private Dimension size;

    public void read(Window window) {
        this.location = window.getLocation();
        this.size = window.getSize();
    }

    public void apply(Window window) {
        if (location != null && size != null) {
            window.setLocation(location);
            window.setSize(size);
        }
    }
}
