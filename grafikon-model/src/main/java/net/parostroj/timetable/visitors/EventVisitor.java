package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.events.*;

/**
 * Event visitor.
 *
 * @author jub
 */
public interface EventVisitor {

    public void visitDiagramEvent(Event event);

    public void visitNetEvent(Event event);

    public void visitNodeEvent(Event event);

    public void visitLineEvent(Event event);

    public void visitTrainEvent(Event event);

    public void visitTrainTypeEvent(Event event);

    public void visitTrainsCycleEvent(Event event);

    public void visitTrainsCycleTypeEvent(Event event);

    public void visitTextItemEvent(Event event);

    public void visitEngineClassEvent(Event event);

    public void visitOutputTemplateEvent(Event event);

    public void visitFreightNetEvent(Event event);

    public void visitOtherEvent(Event event);
}
