package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.events.*;

/**
 * Train names validator.
 *
 * @author jub
 */
public class TrainNamesValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public TrainNamesValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(GTEvent<?> event) {
        if (event instanceof TrainDiagramEvent &&  event.getType() == GTEventType.ATTRIBUTE &&
                event.getAttributeChange().checkName(TrainDiagram.ATTR_TRAIN_NAME_TEMPLATE,
                        TrainDiagram.ATTR_TRAIN_COMPLETE_NAME_TEMPLATE)) {
            this.clearCachedTrainNames();
            return true;
        } else if (event instanceof TrainTypeEvent && event.getType() == GTEventType.ATTRIBUTE &&
                event.getAttributeChange().checkName(TrainType.ATTR_ABBR,
                        TrainType.ATTR_TRAIN_COMPLETE_NAME_TEMPLATE, TrainType.ATTR_TRAIN_NAME_TEMPLATE)) {
            this.clearCachedTrainNames();
            return true;
        }
        return false;
    }

    protected void clearCachedTrainNames() {
        for (Train train : diagram.getTrains()) {
            train.refreshCachedNames();
        }
    }
}
