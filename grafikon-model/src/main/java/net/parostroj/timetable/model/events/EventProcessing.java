package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * @author jub
 */
public class EventProcessing {

    private EventProcessing(){
    }

    public static void visit(Event event, EventVisitor visitor) {
        Object source = event.getSource();
        if (source instanceof TrainDiagram) {
            visitor.visitDiagramEvent(event);
        } else if (source instanceof Net) {
            visitor.visitNetEvent(event);
        } else if (source instanceof Node) {
            visitor.visitNodeEvent(event);
        } else if (source instanceof Line) {
            visitor.visitLineEvent(event);
        } else if (source instanceof Train) {
            visitor.visitTrainEvent(event);
        } else if (source instanceof TrainType) {
            visitor.visitTrainTypeEvent(event);
        } else if (source instanceof TrainsCycle) {
            visitor.visitTrainsCycleEvent(event);
        } else if (source instanceof TrainsCycleType) {
            visitor.visitTrainsCycleTypeEvent(event);
        } else if (source instanceof TextItem) {
            visitor.visitTextItemEvent(event);
        } else if (source instanceof EngineClass) {
            visitor.visitEngineClassEvent(event);
        } else if (source instanceof OutputTemplate) {
            visitor.visitOutputTemplateEvent(event);
        } else if (source instanceof FreightNet) {
            visitor.visitFreightNetEvent(event);
        } else if (source instanceof LineClass) {
            visitor.visitLineClassEvent(event);
        } else if (source instanceof Output) {
            visitor.visitOutputEvent(event);
        } else if (source instanceof TrainTypeCategory) {
            visitor.visitTrainTypeCategoryEvent(event);
        } else {
            visitor.visitOtherEvent(event);
        }
    }

    public static boolean isTypeAndObjectClass(Event event, Event.Type type, Class<?> objectType) {
        return event.getType() == type && objectType.isInstance(event.getObject());
    }

    public static boolean isTypeAndSourceClass(Event event, Event.Type type, Class<?> sourceType) {
        return event.getType() == type && sourceType.isInstance(event.getSource());
    }
}
