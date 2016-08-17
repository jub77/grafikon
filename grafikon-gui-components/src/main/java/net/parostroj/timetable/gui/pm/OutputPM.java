package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.ExecutionMethod;
import org.beanfabrics.model.IBooleanPM;
import org.beanfabrics.model.IOperationPM;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Output;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;

public class OutputPM extends AbstractPM {

    ITextPM name;
    IEnumeratedValuesPM<OutputTemplate> templates;
    IOperationPM create;
    IOperationPM writeBack;
    OperationPM editSelection;
    ITextPM selection;
    IBooleanPM selectionEnabled;
    ModelAttributesPM attributes;
    final IEnumeratedValuesPM<Locale> locale;
    final ITextPM key;

    private WeakReference<TrainDiagram> diagramRef;
    private WeakReference<Output> outputRef;
    private Output newOutput;
    private Collection<? extends ObjectWithId> selectionItems;

    public OutputPM(Collection<Locale> locales) {
        key = new TextPM();
        key.setMandatory(true);
        name = new TextPM();
        name.setMandatory(true);
        templates = new EnumeratedValuesPM<>();
        templates.addPropertyChangeListener("text", evt -> {
            OutputTemplate template = templates.getValue();
            if (template != null) {
                LocalizedString localizedName = template.getName();
                String text = localizedName != null ? localizedName.translate() : template.getKey();
                name.setText(text);
                key.setText(getUniqueKey(template));
            }
        });
        templates.setMandatory(true);
        selection = new TextPM();
        selection.setEditable(false);
        create = new OperationPM();
        writeBack = new OperationPM();
        editSelection = new OperationPM();
        attributes = new ModelAttributesPM();
        selectionEnabled = new BooleanPM();
        locale = new EnumeratedValuesPM<>(EnumeratedValuesPM.createValueMap(
                locales, l -> l.getDisplayName(l)), "-");
        PMManager.setup(this);
    }

    private String getUniqueKey(OutputTemplate template) {
        TrainDiagram diagram = diagramRef.get();
        String key = template.getKey();
        if (diagram != null) {
            String originalKey = key;
            int counter = 0;
            while (!checkUnique(key, diagram)) {
                key = String.format("%s_%d", originalKey, ++counter);
            }
        }
        return key;
    }

    private boolean checkUnique(String key, TrainDiagram diagram) {
        for (Output output : diagram.getOutputs()) {
            if (key.equals(output.getKey())) {
                return false;
            }
        }
        return true;
    }

    public void initNew(TrainDiagram diagram) {
        diagramRef = new WeakReference<>(diagram);
        outputRef = null;
        templates.getOptions().clear();
        name.setText("");
        for (OutputTemplate template : diagram.getOutputTemplates()) {
            String text = Wrapper.getWrapper(template).toString();
            templates.addValue(template, text);
        }
        if (!diagram.getOutputTemplates().isEmpty()) {
            templates.setValue(diagram.getOutputTemplates().iterator().next());
        }
        locale.setValue(null);
    }

    public void init(TrainDiagram diagram, Output output) {
        diagramRef = new WeakReference<>(diagram);
        outputRef = new WeakReference<>(output);
        templates.getOptions().clear();
        name.setText(output.getName().getDefaultString());
        attributes.init(output.getSettings(), Output.CATEGORY_SETTINGS);
        if (output.getTemplate().getSelectionType() == null) {
            this.selection.setText("");
            this.selectionEnabled.setBoolean(false);
        } else {
            this.updateSelection(output.getSelection());
            this.selectionEnabled.setBoolean(true);
        }
        locale.setValue(output.getLocale());
        key.setText(output.getKey());
    }

    public void updateSelection(Collection<? extends ObjectWithId> selectionItems) {
        this.selection.setText(selectionItems != null ? selectionItems.toString() : ResourceLoader.getString("output.selection.all"));
        this.selectionItems = selectionItems;
    }

    public Collection<? extends ObjectWithId> getSelection() {
        return selectionItems;
    }

    @Operation(path = "create")
    public boolean operationCreate() {
        TrainDiagram diagram = diagramRef != null ? diagramRef.get() : null;
        if (diagram != null) {
            newOutput = diagram.getPartFactory().createOutput(IdGenerator.getInstance().getId());
            newOutput.setName(LocalizedString.fromString(name.getText()));
            newOutput.setTemplate(templates.getValue());
            newOutput.setLocale(locale.getValue());
            newOutput.setKey(key.getText());
        }
        return true;
    }

    @Operation(path = "writeBack")
    public boolean operationWriteBack() {
        Output output = outputRef.get();
        if (output != null) {
            output.setName(LocalizedString.fromString(name.getText()));
            // template cannot be changed
            output.setSettings(attributes.getFinalAttributes());
            output.setSelection(selectionItems);
            output.setLocale(locale.getValue());
            output.setKey(key.getText());
        }
        return true;
    }

    @Validation(path = "create")
    public boolean canCreate() {
        return !name.isEmpty() && !templates.isEmpty() && !key.isEmpty();
    }

    @Validation(path = "writeBack")
    public boolean canWriteBack() {
        return !name.isEmpty() && !key.isEmpty();
    }

    @Validation(path = "editSelection")
    public boolean canEditSelection() {
        return selectionEnabled.getBoolean();
    }

    public Output createNewOutput() {
        return newOutput;
    }

    public Output getEditedOutput() {
        return outputRef.get();
    }

    public void setOperationEditSelection(ExecutionMethod executionMethod) {
        editSelection.setExecutionMethods(Collections.<ExecutionMethod>singletonList(executionMethod));
    }
}
