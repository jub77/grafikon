package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

/**
 * Correction of diagram after removal of engine class.
 *
 * @author jub
 */
public class EngineClassRemoveValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public EngineClassRemoveValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED && event.getObject() instanceof EngineClass) {
            EngineClass clazz = (EngineClass) event.getObject();
            // remove engine class from engine cycles
            for (TrainsCycle cycle : diagram.getEngineCycles()) {
                EngineClass eClass = cycle.getAttributes().get(TrainsCycle.ATTR_ENGINE_CLASS, EngineClass.class);
                if (eClass == clazz)
                    cycle.removeAttribute(TrainsCycle.ATTR_ENGINE_CLASS);
            }
            return true;
        }
        return false;
    }

}
