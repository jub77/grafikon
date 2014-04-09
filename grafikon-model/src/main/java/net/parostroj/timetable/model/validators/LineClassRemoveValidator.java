package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.GTEvent;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.NetEvent;

/**
 * @author jub
 */
public class LineClassRemoveValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public LineClassRemoveValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(GTEvent<?> event) {
        if (event instanceof NetEvent && event.getType() == GTEventType.LINE_CLASS_REMOVED) {
            LineClass lineClass = (LineClass) ((NetEvent) event).getObject();
            // remove item with this line class from weight tables
            for (EngineClass eClass : diagram.getEngineClasses()) {
                for (WeightTableRow row : eClass.getWeightTable()) {
                    row.removeWeightInfo(lineClass);
                }
            }
            // remove line class from lines
            for (Line line : diagram.getNet().getLines()) {
                if (line.getAttribute(Line.ATTR_CLASS) == lineClass) {
                    line.removeAttribute(Line.ATTR_CLASS);
                }
                if (line.getAttribute(Line.ATTR_CLASS_BACK) == lineClass) {
                    line.removeAttribute(Line.ATTR_CLASS_BACK);
                }
            }
            return true;
        }
        return false;
    }
}
