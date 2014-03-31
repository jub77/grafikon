package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.*;

/**
 * Validator for freight net.
 *
 * @author jub
 */
public class FreightNetValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public FreightNetValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(GTEvent<?> event) {
        if (event instanceof TrainEvent && event.getType() == GTEventType.ATTRIBUTE
                && event.getAttributeChange().checkName(Train.ATTR_MANAGED_FREIGHT)) {
            TrainEvent tEvent = (TrainEvent) event;
            if (Boolean.TRUE.equals(tEvent.getAttributeChange().getNewValue())) {
                diagram.getFreightNet().addTrain(tEvent.getSource());
            } else {
                diagram.getFreightNet().removeTrain(tEvent.getSource());
            }
        } else if (event instanceof TrainDiagramEvent && event.getType() == GTEventType.TRAIN_REMOVED) {
            TrainDiagramEvent tdEvent = (TrainDiagramEvent) event;
            diagram.getFreightNet().removeTrain((Train) tdEvent.getObject());
        }
        return false;
    }

}
