package net.parostroj.timetable.net;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Visitor that checks if the event belongs to tracked events.
 *
 * @author jub
 */
public class TrackedCheckVisitor implements EventVisitor {

    private boolean tracked = false;

    public void clear() {
        tracked = false;
    }

    public boolean isTracked() {
        return tracked;
    }

    @Override
    public void visit(TrainDiagramEvent event) {
        tracked = true;
    }

    @Override
    public void visit(NetEvent event) {
        tracked = true;
    }

    @Override
    public void visit(NodeEvent event) {
        switch (event.getType()) {
            case ATTRIBUTE: case TRACK_ADDED: case TRACK_ATTRIBUTE: case TRACK_REMOVED:
                tracked = true;
                break;
            default:
                tracked = false;
        }
    }

    @Override
    public void visit(LineEvent event) {
        switch(event.getType()) {
            case ATTRIBUTE: case TRACK_ADDED: case TRACK_ATTRIBUTE: case TRACK_REMOVED:
                tracked = true;
                break;
            default:
                tracked = false;
        }
    }

    @Override
    public void visit(TrainEvent event) {
        switch(event.getType()) {
            case ATTRIBUTE: case TECHNOLOGICAL: case TIME_INTERVAL_LIST:
                tracked = true;
                break;
            default:
                tracked = false;
        }
    }

    @Override
    public void visit(TrainTypeEvent event) {
        tracked = true;
    }

    @Override
    public void visit(TrainsCycleEvent event) {
        tracked = true;
    }

    @Override
    public void visit(TextItemEvent event) {
        tracked = true;
    }
}
