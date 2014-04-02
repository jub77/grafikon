package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.TimeInterval;
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
        if (event instanceof TrainEvent) {
            TrainEvent tEvent = (TrainEvent) event;
            if (event.getType() == GTEventType.ATTRIBUTE
                    && event.getAttributeChange().checkName(Train.ATTR_MANAGED_FREIGHT)) {
                if (Boolean.TRUE.equals(tEvent.getAttributeChange().getNewValue())) {
                    diagram.getFreightNet().addTrain(tEvent.getSource());
                } else {
                    diagram.getFreightNet().removeTrain(tEvent.getSource());
                }
            }
            if (this.isManaged(tEvent.getSource())) {
                if (event.getType() == GTEventType.TIME_INTERVAL_ATTRIBUTE
                        && event.getAttributeChange().checkName(TimeInterval.ATTR_NOT_MANAGED_FREIGHT)) {
                    diagram.getFreightNet().checkNode(tEvent.getSource());
                } else if (event.getType() == GTEventType.TIME_INTERVAL_LIST) {
                    diagram.getFreightNet().checkNode(tEvent.getSource());
                }
            }
        } else if (event instanceof TrainDiagramEvent && event.getType() == GTEventType.TRAIN_REMOVED) {
            TrainDiagramEvent tdEvent = (TrainDiagramEvent) event;
            Train train = (Train) tdEvent.getObject();
            if (this.isManaged(train)) {
                diagram.getFreightNet().removeTrain(train);
            }
        }
        return false;
    }

    private boolean isManaged(Train train) {
        return train.getAttributes().getBool(Train.ATTR_MANAGED_FREIGHT);
    }
}
