package net.parostroj.timetable.model.changes;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Visitor that checks if the event belongs to tracked events.
 *
 * @author jub
 */
public class TrackedCheckVisitor implements EventVisitor {

    private boolean tracked = false;

    public boolean isTracked() {
        boolean t = tracked;
        tracked = false;
        return t;
    }

    @Override
    public void visit(TrainDiagramEvent event) {
        switch (event.getType()) {
            case TRAIN_TYPE_MOVED: case TEXT_ITEM_MOVED: case ENGINE_CLASS_MOVED: case OUTPUT_TEMPLATE_MOVED:
                tracked = false;
                break;
            default:
                tracked = true;
        }
    }

    @Override
    public void visit(NetEvent event) {
        if (event.getType() == GTEventType.LINE_CLASS_MOVED)
            tracked = false;
        else
            tracked = true;
    }

    @Override
    public void visit(FreightNetEvent event) {
        switch (event.getType()) {
            case FREIGHT_NET_CONNECTION_ADDED:
            case FREIGHT_NET_CONNECTION_REMOVED:
                tracked = true;
                break;
            default:
                tracked = false;
                break;
        }
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
            case ATTRIBUTE: case TECHNOLOGICAL: case TIME_INTERVAL_LIST: case TIME_INTERVAL_ATTRIBUTE:
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

    @Override
    public void visit(OutputTemplateEvent event) {
        tracked = true;
    }

    @Override
    public void visit(EngineClassEvent event) {
        tracked = true;
    }
}
