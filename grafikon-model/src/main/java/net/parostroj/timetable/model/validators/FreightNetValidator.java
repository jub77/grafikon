package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.*;
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
        if (event.getSource() instanceof Train train) {
            if (event.getType() == Type.ATTRIBUTE
                    && event.getAttributeChange().checkName(Train.ATTR_MANAGED_FREIGHT_OVERRIDE)) {
                if (!Boolean.TRUE.equals(event.getAttributeChange().getNewValue())) {
                    diagram.getFreightNet().checkTrain(train);
                }
                return true;
            }
            if (train.getManagedFreight() != ManagedFreight.NONE) {
                checkTrainWithManagedFreight(event, train);
                return true;
            }
        } else if (event.getSource() instanceof TrainType type && event.getType() == Type.ATTRIBUTE && event.getAttributeChange().checkName(TrainType.ATTR_MANAGED_FREIGHT)) {
            for (Train train : diagram.getTrains()) {
                if (train.getType() == type) {
                    diagram.getFreightNet().checkTrain(train);
                }
            }
        } else if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED && event.getObject() instanceof Train train) {
            if (train.getManagedFreight() != ManagedFreight.NONE) {
                diagram.getFreightNet().removeTrain(train);
                return true;
            }
        }
        return false;
    }

    private void checkTrainWithManagedFreight(Event event, Train train) {
        if (event.getType() == Event.Type.OBJECT_ATTRIBUTE && event.getObject() instanceof TimeInterval
                && event.getAttributeChange().checkName(TimeInterval.ATTR_MANAGED_FREIGHT_OVERRIDE)
                || event.getType() == Event.Type.SPECIAL && event.getData() instanceof SpecialTrainTimeIntervalList) {
            diagram.getFreightNet().checkTrain(train);
        }
    }
}
