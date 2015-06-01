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

    // list of attributes distributed through sequence of circulations
    private static final String[] CHECKED_ATTRIBUTES = new String[] {
            TrainsCycle.ATTR_ENGINE_CLASS,
            TrainsCycle.ATTR_COMPANY,
            TrainsCycle.ATTR_DESCRIPTION };

    private boolean changing;

    @Override
    public boolean validate(GTEvent<?> event) {
        boolean validated = false;
        if (event instanceof TrainsCycleEvent) {
            validated = handleTrainsCycleEvent(event);
        }
        if (event instanceof TrainDiagramEvent && event.getType() == GTEventType.TRAINS_CYCLE_REMOVED) {
            TrainsCycle deleted = (TrainsCycle) ((TrainDiagramEvent) event).getObject();
            // handle sequence of circulations
            if (deleted.isPartOfSequence()) {
                deleted.removeFromSequence();
            }
            validated = true;
        }
        return validated;
    }

    private boolean handleTrainsCycleEvent(GTEvent<?> event) {
        boolean validated = false;
        TrainsCycleEvent tcEvent = (TrainsCycleEvent) event;
        TrainsCycle circulation = tcEvent.getSource();
        boolean attribute = event.getType() == GTEventType.ATTRIBUTE;
        if (attribute && tcEvent.getSource().isPartOfSequence()
                && event.getAttributeChange().checkName(CHECKED_ATTRIBUTES)) {
            distributeAttributesInSequence(circulation, tcEvent.getAttributeChange());
            validated = true;
        }
        if (attribute && event.getAttributeChange().checkName(TrainsCycle.ATTR_ENGINE_CLASS)) {
            recalculateEngineClassChange(circulation);
            validated = true;
        }
        return validated;
    }

    private void distributeAttributesInSequence(TrainsCycle circulation, AttributeChange attrChange) {
        // suppress events if the change is initiated by validator
        if (!changing) {
            changing = true;
            try {
                // synchronize the same attribute for all circulations in sequence
                circulation.applyToSequence(tc -> {
                    if (TrainsCycle.ATTR_DESCRIPTION.equals(attrChange.getName())) {
                        // special handling of description
                        tc.setDescription((String) attrChange.getNewValue());
                    } else {
                        tc.getAttributes().setRemove(attrChange.getName(), attrChange.getNewValue());
                    }
                });
            } finally {
                changing = false;
            }
        }
    }

    private void recalculateEngineClassChange(TrainsCycle circulation) {
        for (TrainsCycleItem item : new ArrayList<TrainsCycleItem>(circulation.getItems())) {
            item.getTrain().recalculate();
        }
    }
}
