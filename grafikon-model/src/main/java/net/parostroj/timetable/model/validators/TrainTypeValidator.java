package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainTypeEvent;

public class TrainTypeValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public TrainTypeValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(GTEvent<?> event) {
        if (event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().checkName(TrainType.ATTR_CATEGORY)) {
            for (Train train : diagram.getTrains()) {
                if (train.getType() == ((TrainTypeEvent) event).getSource()) {
                    train.recalculate();
                }
            }
            return true;
        }
        return false;
    }

}
