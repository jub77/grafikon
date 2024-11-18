package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.events.Event;

import java.util.List;

/**
 * Removal of train (remove train from circulations).
 *
 * @author jub
 */
public class TrainRemoveValidator implements TrainDiagramValidator {
    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram diagram
                && event.getType() == Event.Type.REMOVED
                && event.getObject() instanceof Train train) {
            for (TrainsCycleType type : diagram.getCycleTypes()) {
                removeFromCirculation(train.getCycles(type));
            }
            return true;
        }
        return false;
    }

    private void removeFromCirculation(List<TrainsCycleItem> items) {
        if (!items.isEmpty()) {
            for (TrainsCycleItem item : List.copyOf(items)) {
                item.getCycle().removeItem(item);
            }
        }
    }
}
