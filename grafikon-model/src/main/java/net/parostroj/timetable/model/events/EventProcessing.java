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
        switch (source) {
            case TrainDiagram ignored -> visitor.visitDiagramEvent(event);
            case Net ignored -> visitor.visitNetEvent(event);
            case Node ignored -> visitor.visitNodeEvent(event);
            case Line ignored -> visitor.visitLineEvent(event);
            case Train ignored -> visitor.visitTrainEvent(event);
            case TrainType ignored -> visitor.visitTrainTypeEvent(event);
            case TrainsCycle ignored -> visitor.visitTrainsCycleEvent(event);
            case TrainsCycleType ignored -> visitor.visitTrainsCycleTypeEvent(event);
            case TextItem ignored -> visitor.visitTextItemEvent(event);
            case EngineClass ignored -> visitor.visitEngineClassEvent(event);
            case OutputTemplate ignored -> visitor.visitOutputTemplateEvent(event);
            case FreightNet ignored -> visitor.visitFreightNetEvent(event);
            case LineClass ignored -> visitor.visitLineClassEvent(event);
            case Output ignored -> visitor.visitOutputEvent(event);
            case TrainTypeCategory ignored -> visitor.visitTrainTypeCategoryEvent(event);
            case TrackConnector ignored -> visitor.visitTrackConnectorEvent(event);
            case null, default -> visitor.visitOtherEvent(event);
        }
    }

    public static boolean isTypeAndObjectClass(Event event, Event.Type type, Class<?> objectType) {
        return event.getType() == type && objectType.isInstance(event.getObject());
    }

    public static boolean isTypeAndSourceClass(Event event, Event.Type type, Class<?> sourceType) {
        return event.getType() == type && sourceType.isInstance(event.getSource());
    }
}
