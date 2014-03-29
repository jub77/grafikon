package net.parostroj.timetable.gui.views.graph;

import java.awt.Point;
import java.awt.event.ActionEvent;

/**
 * Extended action with location information.
 *
 * @author jub
 */
public class ActionEventWithLocation extends ActionEvent {

    private Point location;

    public ActionEventWithLocation(Object source, int id, String command) {
        super(source, id, command);
    }

    public ActionEventWithLocation(Object source, int id, String command, Point location) {
        this(source, id, command);
        this.location = location;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
