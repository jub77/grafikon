package net.parostroj.timetable.model.validators;

import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainEvent;

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
            Map<String, List<TrainsCycleItem>> map = trainEvent.getSource().getCyclesMap();
            for (List<TrainsCycleItem> iList : map.values()) {
                for (TrainsCycleItem item : iList) {
                    item.getCycle().correctItem(item);
                }
            }
            return true;
        }
        return false;
    }
}
