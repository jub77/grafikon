package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.events.*;

/**
 * Abstract event visitor.
 *
 * @author jub
 */
public abstract class AbstractEventVisitor implements EventVisitor {

    @Override
    public void visitDiagramEvent(Event event) {
    }

    @Override
    public void visitNetEvent(Event event) {
    }

    @Override
    public void visitNodeEvent(Event event) {
    }

    @Override
    public void visitLineEvent(Event event) {
    }

    @Override
    public void visitTrainEvent(Event event) {
    }

    @Override
    public void visitTrainTypeEvent(Event event) {
    }

    @Override
    public void visitTrainsCycleEvent(Event event) {
    }

    @Override
    public void visitTrainsCycleTypeEvent(Event event) {
    }

    @Override
    public void visitTextItemEvent(Event event) {
    }

    @Override
    public void visitEngineClassEvent(Event event) {
    }

    @Override
    public void visitOutputTemplateEvent(Event event) {
    }

    @Override
    public void visitOutputEvent(Event event) {
    }

    @Override
    public void visitFreightNetEvent(Event event) {
    }

    @Override
    public void visitOtherEvent(Event event) {
    }

    @Override
    public void visitLineClassEvent(Event event) {
    }
}
