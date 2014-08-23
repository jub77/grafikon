package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainEvent;

import com.google.common.collect.ListMultimap;

/**
 * Validator when train time intervals change.
 *
 * @author jub
 */
public class TrainIntervalsValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(GTEvent<?> event) {
        // keep circulations sorted
        if (event instanceof TrainEvent && event.getType() == GTEventType.TIME_INTERVAL_LIST) {
            TrainEvent trainEvent = (TrainEvent) event;
            ListMultimap<TrainsCycleType, TrainsCycleItem> map = trainEvent.getSource().getCyclesMap();
            for (TrainsCycleItem item : map.values()) {
                item.getCycle().correctItem(item);
            }
            return true;
        }
        return false;
    }
}
