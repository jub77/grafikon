package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.IOperationPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;

import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.Output;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;

public class OutputPM extends AbstractPM {

    ITextPM name;
    IEnumeratedValuesPM<OutputTemplate> templates;
    IOperationPM create;

    private WeakReference<TrainDiagram> diagramRef;
    private Output newOutput;

    public OutputPM() {
        name = new TextPM();
        name.setMandatory(true);
        templates = new EnumeratedValuesPM<>();
        create = new OperationPM();
        PMManager.setup(this);
    }

    public void initNew(TrainDiagram diagram) {
        diagramRef = new WeakReference<TrainDiagram>(diagram);
        templates.getOptions().clear();
        name.setText("");
        for (OutputTemplate template : diagram.getOutputTemplates()) {
            String text = Wrapper.getWrapper(template).toString();
            templates.addValue(template, text);
        }
        if (!diagram.getOutputTemplates().isEmpty()) {
            templates.setValue(diagram.getOutputTemplates().get(0));
        }
    }

    @Operation(path = "create")
    public boolean operationCreate() {
        TrainDiagram diagram = diagramRef != null ? diagramRef.get() : null;
        if (diagram != null) {
            newOutput = diagram.getPartFactory().createOutput(IdGenerator.getInstance().getId());
            newOutput.setName(LocalizedString.fromString(name.getText()));
            newOutput.setTemplate(templates.getValue());
        }
        return true;
    }

    @Validation(path = "create")
    public boolean canCreate() {
        return !name.isEmpty();
    }

    public Output createNewOutput() {
        return newOutput;
    }
}
