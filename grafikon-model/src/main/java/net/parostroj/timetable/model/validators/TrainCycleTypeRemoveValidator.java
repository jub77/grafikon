package net.parostroj.timetable.model.validators;

import java.util.ArrayList;
import java.util.List;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;

public class TrainCycleTypeRemoveValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public TrainCycleTypeRemoveValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED
                && event.getObject() instanceof TrainsCycleType) {
            // remove all cycles ...
            List<TrainsCycle> copy = new ArrayList<TrainsCycle>(((TrainsCycleType) event.getObject()).getCycles());
            for (TrainsCycle cycle : copy) {
                diagram.removeCycle(cycle);
            }
            return true;
        }
        return false;
    }

}
