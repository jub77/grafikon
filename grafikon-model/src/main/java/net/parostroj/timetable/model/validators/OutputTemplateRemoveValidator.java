package net.parostroj.timetable.model.validators;

import java.util.ArrayList;
import java.util.List;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.model.events.Event.Type;

/**
 * Correction of diagram after removal of output template.
 *
 * @author jub
 */
public class OutputTemplateRemoveValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;

    public OutputTemplateRemoveValidator(TrainDiagram diagram) {
        this.diagram = diagram;
    }

    @Override
    public boolean validate(Event event) {
        if (event.getSource() instanceof TrainDiagram && event.getType() == Type.REMOVED
                && event.getObject() instanceof OutputTemplate outputTemplate) {
            removeOutputsForTemplate(outputTemplate);
            return true;
        }
        return false;
    }

    private void removeOutputsForTemplate(OutputTemplate outputTemplate) {
        List<Output> outputsToRemove = null;
        // output if the template for that is removed
        for (Output output : diagram.getOutputs()) {
            if (output.getTemplate() == outputTemplate) {
                if (outputsToRemove == null) { outputsToRemove = new ArrayList<>(); }
                outputsToRemove.add(output);
            }
        }
        if (outputsToRemove != null) {
            for (Output output : outputsToRemove) {
                diagram.getOutputs().remove(output);
            }
        }
    }

}
