package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

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
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram && event.getType() == Type.ATTRIBUTE &&
                event.getAttributeChange().checkName(TrainDiagram.ATTR_TRAIN_NAME_TEMPLATE,
                        TrainDiagram.ATTR_TRAIN_COMPLETE_NAME_TEMPLATE)) {
            this.clearCachedTrainNames(null);
            return true;
        } else if (event.getSource() instanceof TrainType && event.getType() == Type.ATTRIBUTE &&
                event.getAttributeChange().checkName(TrainType.ATTR_ABBR,
                        TrainType.ATTR_TRAIN_COMPLETE_NAME_TEMPLATE, TrainType.ATTR_TRAIN_NAME_TEMPLATE)) {
            this.clearCachedTrainNames((TrainType) event.getSource());
            return true;
        }
        return false;
    }

    protected void clearCachedTrainNames(TrainType type) {
        for (Train train : diagram.getTrains()) {
            if (type != null && type == train.getType()) {
                train.refreshCachedNames();
            } else if (type == null) {
                TrainType tType = train.getType();
                if (tType != null) {
                    if (tType.getTrainCompleteNameTemplate() == null || tType.getTrainNameTemplate() == null) {
                        train.refreshCachedNames();
                    }
                }
            }
        }
    }
}
