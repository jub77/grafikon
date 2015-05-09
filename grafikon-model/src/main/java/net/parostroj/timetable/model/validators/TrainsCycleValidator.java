package net.parostroj.timetable.model.validators;

import java.util.ArrayList;

import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.events.*;

/**
 * Validator for circulation.
 *
 * @author jub
 */
public class TrainsCycleValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(GTEvent<?> event) {
        if (event instanceof TrainsCycleEvent && event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().checkName(TrainsCycle.ATTR_ENGINE_CLASS)) {
            TrainsCycleEvent tcEvent = (TrainsCycleEvent) event;
            for (TrainsCycleItem item : new ArrayList<TrainsCycleItem>(tcEvent.getSource().getItems())) {
                item.getTrain().recalculate();
            }
            return true;
        }
        if (event instanceof TrainDiagramEvent && event.getType() == GTEventType.TRAINS_CYCLE_REMOVED) {
            TrainsCycle deleted = ((TrainsCycleEvent) event).getSource();
            // handle sequence of circulations
            if (deleted.isPartOfSequence()) {
                deleted.removeFromSequence();
            }
        }
        return false;
    }

}
