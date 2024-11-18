package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

public class TrainTypeValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public TrainTypeValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainType && event.getType() == Type.ATTRIBUTE
                && event.getAttributeChange().checkName(TrainType.ATTR_CATEGORY)) {
            for (Train train : diagram.getTrains()) {
                if (train.getType() == event.getSource()) {
                    train.recalculate();
                }
            }
            return true;
        } else if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED
                && event.getObject() instanceof TrainType type) {
            for (Train train : diagram.getTrains()) {
                if (train.getType() == type) {
                    train.setType(null);
                }
            }
        }
        return false;
    }

}
