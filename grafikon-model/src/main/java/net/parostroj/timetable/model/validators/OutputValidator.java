package net.parostroj.timetable.model.validators;

import net.parostroj.timetable.model.ModelObjectType;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Output;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Event.Type;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Correction of output after removal of objects.
 *
 * @author jub
 */
public class OutputValidator implements TrainDiagramValidator {

    private final TrainDiagram diagram;
    private final Set<Class<?>> classes;

    public OutputValidator(TrainDiagram diagram) {
        this.diagram = diagram;
        this.classes = Arrays.stream(ModelObjectType.values())
                .map(ModelObjectType::getType).collect(Collectors.toSet());
    }

    @Override
    public boolean validate(Event event) {
        if (event.getType() == Type.REMOVED
                && classes.contains(event.getObject().getClass())) {
            boolean changed = false;
            Object object = event.getObject();
            for (Output output : diagram.getOutputs()) {
                if (output.getTemplate() != null && output.getTemplate().getSelectionType() != null) {
                    if (output.getTemplate().getSelectionType().getType() == object.getClass()
                            && output.getAttributes().get(Output.ATTR_SELECTION) != null) {
                        Collection<ObjectWithId> selection = output.getSelection();
                        if (selection.stream().anyMatch(i -> i == object)) {
                            output.setSelection(
                                    selection.stream().filter(i -> i != object).collect(Collectors.toList()));
                            changed = true;
                        }
                    }
                }
            }
            return changed;
        }
        return false;
    }
}
