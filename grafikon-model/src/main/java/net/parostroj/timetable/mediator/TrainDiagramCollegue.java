package net.parostroj.timetable.mediator;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainDiagramListenerWithNested;

/**
 * TrainDiagram colleague - send messages for TrainDiagramEvent.
 *
 * @author jub
 */
public class TrainDiagramCollegue extends AbstractColleague implements TrainDiagramListenerWithNested {

    private TrainDiagram diagram;

    public TrainDiagramCollegue() {}

    public TrainDiagramCollegue(TrainDiagram diagram) {
        this.setTrainDiagram(diagram);
    }

    public void setTrainDiagram(TrainDiagram diagram) {
        if (this.diagram != null) {
            this.diagram.removeListenerWithNested(this);
        }

        this.diagram = diagram;
        if (this.diagram != null) {
            this.diagram.addListenerWithNested(this);
        }
    }

    @Override
    public void receiveMessage(Object message) {
        // do not react to any event
    }

    @Override
    public void trainDiagramChangedNested(TrainDiagramEvent event) {
        // process and distribute all events
        this.sendMessage(event);
    }

    @Override
    public void trainDiagramChanged(TrainDiagramEvent event) {
        // process and distribute all events
        this.sendMessage(event);
    }
}
