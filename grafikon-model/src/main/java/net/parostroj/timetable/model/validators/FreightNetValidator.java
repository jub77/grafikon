package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

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
    public boolean validate(Event event) {
        if (event.getSource() instanceof Train) {
            Train train = (Train) event.getSource();
            if (event.getType() == Type.ATTRIBUTE
                    && event.getAttributeChange().checkName(Train.ATTR_MANAGED_FREIGHT)) {
                if (!Boolean.TRUE.equals(event.getAttributeChange().getNewValue())) {
                    diagram.getFreightNet().checkTrain(train);
                }
                return true;
            }
            if (train.isManagedFreight()) {
                checkTrainWithManagedFreight(event, train);
                return true;
            }
        } else if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED && event.getObject() instanceof Train) {
            Train train = (Train) event.getObject();
            if (train.isManagedFreight()) {
                diagram.getFreightNet().removeTrain(train);
                return true;
            }
        }
        return false;
    }

    private void checkTrainWithManagedFreight(Event event, Train train) {
        if (event.getType() == Event.Type.OBJECT_ATTRIBUTE && event.getObject() instanceof TimeInterval
                && event.getAttributeChange().checkName(TimeInterval.ATTR_NOT_MANAGED_FREIGHT)
                || event.getType() == Event.Type.SPECIAL && event.getData() instanceof SpecialTrainTimeIntervalList) {
            diagram.getFreightNet().checkTrain(train);
        }
    }
}
