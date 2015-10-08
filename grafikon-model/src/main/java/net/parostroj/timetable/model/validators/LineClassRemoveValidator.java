package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

/**
 * @author jub
 */
public class LineClassRemoveValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public LineClassRemoveValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof Net && event.getType() == Type.REMOVED && event.getObject() instanceof LineClass) {
            LineClass lineClass = (LineClass) event.getObject();
            // remove item with this line class from weight tables
            for (EngineClass eClass : diagram.getEngineClasses()) {
                for (WeightTableRow row : eClass.getWeightTable()) {
                    row.removeWeightInfo(lineClass);
                }
            }
            // remove line class from lines
            for (Line line : diagram.getNet().getLines()) {
                if (line.getAttribute(Line.ATTR_CLASS, LineClass.class) == lineClass) {
                    line.removeAttribute(Line.ATTR_CLASS);
                }
                if (line.getAttribute(Line.ATTR_CLASS_BACK, LineClass.class) == lineClass) {
                    line.removeAttribute(Line.ATTR_CLASS_BACK);
                }
            }
            return true;
        }
        return false;
    }
}
