package net.parostroj.timetable.model.validators;

import java.util.ArrayList;

import net.parostroj.timetable.model.EngineClass;
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
            TrainsCycle circulation = tcEvent.getSource();
            for (TrainsCycleItem item : new ArrayList<TrainsCycleItem>(circulation.getItems())) {
                item.getTrain().recalculate();
            }
            if (circulation.isPartOfSequence()) {
                // synchronize the same engine class for all circulations in sequence
                TrainsCycle current = circulation;
                EngineClass engineClass = circulation.getAttribute(TrainsCycle.ATTR_ENGINE_CLASS, EngineClass.class);
                while (current.getNext() != circulation) {
                    current.getAttributes().setRemove(TrainsCycle.ATTR_ENGINE_CLASS, engineClass);
                    current = current.getNext();
                }
            }
            return true;
        }
        if (event instanceof TrainDiagramEvent && event.getType() == GTEventType.TRAINS_CYCLE_REMOVED) {
            TrainsCycle deleted = (TrainsCycle) ((TrainDiagramEvent) event).getObject();
            // handle sequence of circulations
            if (deleted.isPartOfSequence()) {
                deleted.removeFromSequence();
            }
            return true;
        }
        return false;
    }

}
