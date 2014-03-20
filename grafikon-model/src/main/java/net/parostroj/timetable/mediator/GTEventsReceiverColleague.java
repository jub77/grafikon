package net.parostroj.timetable.mediator;

import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.LineEvent;
import net.parostroj.timetable.model.events.NetEvent;
import net.parostroj.timetable.model.events.NodeEvent;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainEvent;
import net.parostroj.timetable.model.events.TrainTypeEvent;
import net.parostroj.timetable.model.events.TrainsCycleEvent;

/**
 * Colleague for receiving and processing events.
 *
 * @author jub
 */
public class GTEventsReceiverColleague extends AbstractColleague {

    private final boolean theMostNested;

    public GTEventsReceiverColleague() {
        this(true);
    }

    public GTEventsReceiverColleague(boolean theMostNested) {
        this.theMostNested = theMostNested;
    }

    @Override
    public void receiveMessage(Object message) {
        if (message instanceof GTEvent<?>) {
            GTEvent<?> event = (GTEvent<?>) message;
            if (theMostNested) {
                processGTEventImpl(event.getLastNestedEvent());
            } else {
                for (GTEvent<?> e : event) {
                    processGTEventImpl(e);
                }
            }
        }
    }

    private void processGTEventImpl(GTEvent<?> event) {
        // process by specific method
        if (event instanceof LineEvent)
            processLineEvent((LineEvent)event);
        else if (event instanceof NetEvent)
            processNetEvent((NetEvent)event);
        else if (event instanceof NodeEvent)
            processNodeEvent((NodeEvent)event);
        else if (event instanceof TrainDiagramEvent)
            processTrainDiagramEvent((TrainDiagramEvent)event);
        else if (event instanceof TrainEvent)
            processTrainEvent((TrainEvent)event);
        else if (event instanceof TrainTypeEvent)
            processTrainTypeEvent((TrainTypeEvent)event);
        else if (event instanceof TrainsCycleEvent)
            processTrainsCycleEvent((TrainsCycleEvent)event);
        else
            processGTEvent(event);
        // process by common method
        processGTEventAll(event);
    }

    public void processLineEvent(LineEvent event) {}

    public void processNetEvent(NetEvent event) {}

    public void processNodeEvent(NodeEvent event) {}

    public void processTrainDiagramEvent(TrainDiagramEvent event) {}

    public void processTrainEvent(TrainEvent event) {}

    public void processTrainTypeEvent(TrainTypeEvent event) {}

    public void processTrainsCycleEvent(TrainsCycleEvent event) {}

    public void processGTEvent(GTEvent<?> event) {}

    public void processGTEventAll(GTEvent<?> event) {}
}
