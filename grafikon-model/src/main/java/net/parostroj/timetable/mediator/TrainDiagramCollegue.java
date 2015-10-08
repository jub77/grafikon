package net.parostroj.timetable.mediator;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.*;

/**
 * TrainDiagram colleague - send messages for TrainDiagramEvent.
 *
 * @author jub
 */
public class TrainDiagramCollegue extends AbstractColleague implements Listener {

    private TrainDiagram diagram;

    public TrainDiagramCollegue() {}

    public TrainDiagramCollegue(TrainDiagram diagram) {
        this.setTrainDiagram(diagram);
    }

    public void setTrainDiagram(TrainDiagram diagram) {
        if (this.diagram != null) {
            this.diagram.removeAllEventListener(this);
        }

        this.diagram = diagram;
        if (this.diagram != null) {
            this.diagram.addAllEventListener(this);
        }
    }

    @Override
    public void receiveMessage(Object message) {
        // do not react to any event
    }

    @Override
    public void changed(Event event) {
        // process and distribute all events
        this.sendMessage(event);
    }
}
