package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.events.*;

/**
 * Event visitor.
 *
 * @author jub
 */
public interface EventVisitor {

    default void visitDiagramEvent(Event event) {}

    default void visitNetEvent(Event event) {}

    default void visitNodeEvent(Event event) {}

    default void visitLineEvent(Event event) {}

    default void visitTrainEvent(Event event) {}

    default void visitTrainTypeEvent(Event event) {}

    default void visitTrainsCycleEvent(Event event) {}

    default void visitTrainsCycleTypeEvent(Event event) {}

    default void visitTextItemEvent(Event event) {}

    default void visitEngineClassEvent(Event event) {}

    default void visitOutputTemplateEvent(Event event) {}

    default void visitOutputEvent(Event event) {}

    default void visitFreightNetEvent(Event event) {}

    default void visitLineClassEvent(Event event) {}

    default void visitOtherEvent(Event event) {}

    default void visitTrainTypeCategoryEvent(Event event) {}

    default void visitTrackConnectorEvent(Event event) {}
}
