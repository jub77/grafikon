package net.parostroj.timetable.model.validators;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;

public class TrainCycleTypeRemoveValidator implements TrainDiagramValidator {

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED
                && event.getObject() instanceof TrainsCycleType cycleType) {
            // remove all cycles ...
            List<TrainsCycle> copy = ImmutableList.copyOf(cycleType.getCycles());
            for (TrainsCycle cycle : copy) {
                cycleType.getCycles().remove(cycle);
            }
            return true;
        }
        return false;
    }

}
