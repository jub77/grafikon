package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.events.*;

/**
 * Event visitor.
 *
 * @author jub
 */
public interface EventVisitor {

    void visitDiagramEvent(Event event);

    void visitNetEvent(Event event);

    void visitNodeEvent(Event event);

    void visitLineEvent(Event event);

    void visitTrainEvent(Event event);

    void visitTrainTypeEvent(Event event);

    void visitTrainsCycleEvent(Event event);

    void visitTrainsCycleTypeEvent(Event event);

    void visitTextItemEvent(Event event);

    void visitEngineClassEvent(Event event);

    void visitOutputTemplateEvent(Event event);

    void visitOutputEvent(Event event);

    void visitFreightNetEvent(Event event);

    void visitLineClassEvent(Event event);

    void visitOtherEvent(Event event);

    void visitTrainTypeCategoryEvent(Event event);
}
