package net.parostroj.timetable.mediator;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Colleague for receiving and processing events.
 *
 * @author jub
 */
public class GTEventsReceiverColleague extends AbstractColleague {

    private final EventVisitor visitor;

    public GTEventsReceiverColleague() {
        this.visitor = new EventVisitor() {
            @Override
            public void visitOtherEvent(Event event) {
                processGTEvent(event);
            }

            @Override
            public void visitOutputEvent(Event event) {
                processGTEvent(event);
            }

            @Override
            public void visitOutputTemplateEvent(Event event) {
                processGTEvent(event);
            }

            @Override
            public void visitEngineClassEvent(Event event) {
                processGTEvent(event);
            }

            @Override
            public void visitTextItemEvent(Event event) {
                processGTEvent(event);
            }

            @Override
            public void visitTrainsCycleEvent(Event event) {
                processTrainsCycleEvent(event);
            }

            @Override
            public void visitTrainsCycleTypeEvent(Event event) {
                processTrainsCycleTypeEvent(event);
            }

            @Override
            public void visitTrainTypeEvent(Event event) {
                processTrainTypeEvent(event);
            }

            @Override
            public void visitTrainEvent(Event event) {
                processTrainEvent(event);
            }

            @Override
            public void visitLineEvent(Event event) {
                processLineEvent(event);
            }

            @Override
            public void visitNodeEvent(Event event) {
                processNodeEvent(event);
            }

            @Override
            public void visitNetEvent(Event event) {
                processNetEvent(event);
            }

            @Override
            public void visitDiagramEvent(Event event) {
                processTrainDiagramEvent(event);
            }

            @Override
            public void visitFreightNetEvent(Event event) {
                processFreightNetEvent(event);
            }

            @Override
            public void visitLineClassEvent(Event event) {
                processLineClassEvent(event);
            }

            @Override
            public void visitTrainTypeCategoryEvent(Event event) {
                processTrainTypeCategoryEvent(event);
            }
        };
    }

    @Override
    public void receiveMessage(Object message) {
        if (message instanceof Event) {
            processGTEventImpl((Event) message);
        }
    }

    private void processGTEventImpl(Event event) {
        // process by specific method
        EventProcessing.visit(event, visitor);
        // process by common method
        processGTEventAll(event);
    }

    public void processLineEvent(Event event) {}

    public void processNetEvent(Event event) {}

    public void processNodeEvent(Event event) {}

    public void processTrainDiagramEvent(Event event) {}

    public void processTrainEvent(Event event) {}

    public void processTrainTypeEvent(Event event) {}

    public void processTrainsCycleEvent(Event event) {}

    public void processTrainsCycleTypeEvent(Event event) {}

    public void processFreightNetEvent(Event event) {}

    public void processGTEvent(Event event) {}

    public void processGTEventAll(Event event) {}

    public void processLineClassEvent(Event event) {}

    public void processTrainTypeCategoryEvent(Event event) {}
}
