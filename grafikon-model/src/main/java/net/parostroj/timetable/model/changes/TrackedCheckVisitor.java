package net.parostroj.timetable.model.changes;

import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.Track;
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
    public void visitDiagramEvent(Event event) {
        switch (event.getType()) {
            case MOVED:
                tracked = false;
                break;
            default:
                tracked = true;
        }
    }

    @Override
    public void visitNetEvent(Event event) {
        if (event.getType() == Event.Type.MOVED) {
            tracked = false;
        } else {
            tracked = true;
        }
    }

    @Override
    public void visitFreightNetEvent(Event event) {
        switch (event.getType()) {
            case ADDED:
            case REMOVED:
                tracked = event.getObject() instanceof FNConnection;
                break;
            default:
                tracked = false;
                break;
        }
    }

    @Override
    public void visitNodeEvent(Event event) {
        switch (event.getType()) {
            case ATTRIBUTE:
                tracked = true;
                break;
            case ADDED: case OBJECT_ATTRIBUTE: case REMOVED:
                tracked = event.getObject() instanceof Track;
                break;
            default:
                tracked = false;
        }
    }

    @Override
    public void visitLineEvent(Event event) {
        switch(event.getType()) {
            case ATTRIBUTE:
                tracked = true;
                break;
            case ADDED: case OBJECT_ATTRIBUTE: case REMOVED:
                tracked = event.getObject() instanceof Track;
                break;
            default:
                tracked = false;
        }
    }

    @Override
    public void visitTrainEvent(Event event) {
        switch(event.getType()) {
            case ATTRIBUTE: case OBJECT_ATTRIBUTE: case SPECIAL:
                tracked = true;
                break;
            default:
                tracked = false;
        }
    }

    @Override
    public void visitTrainTypeEvent(Event event) {
        tracked = true;
    }

    @Override
    public void visitTrainsCycleEvent(Event event) {
        tracked = true;
    }

    @Override
    public void visitTrainsCycleTypeEvent(Event event) {
        tracked = true;
    }

    @Override
    public void visitTextItemEvent(Event event) {
        tracked = true;
    }

    @Override
    public void visitOutputTemplateEvent(Event event) {
        tracked = true;
    }

    @Override
    public void visitEngineClassEvent(Event event) {
        tracked = true;
    }

    @Override
    public void visitLineClassEvent(Event event) {
        tracked = false;
    }

    @Override
    public void visitOtherEvent(Event event) {
        tracked = false;
    }
}
