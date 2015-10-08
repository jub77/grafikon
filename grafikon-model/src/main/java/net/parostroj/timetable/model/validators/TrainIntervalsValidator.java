package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.events.*;

import com.google.common.collect.ListMultimap;

/**
 * Validator when train time intervals change.
 *
 * @author jub
 */
public class TrainIntervalsValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(Event event) {
        // keep circulations sorted
        if (event.getSource() instanceof Train && event.getType() == Event.Type.SPECIAL && event.getData() instanceof SpecialTrainTimeIntervalList) {
            ListMultimap<TrainsCycleType, TrainsCycleItem> map = ((Train) event.getSource()).getCyclesMap();
            for (TrainsCycleItem item : map.values()) {
                item.getCycle().correctItem(item);
            }
            return true;
        }
        return false;
    }
}
