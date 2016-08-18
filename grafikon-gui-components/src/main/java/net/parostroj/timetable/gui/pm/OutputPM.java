package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    LocalizedStringDefaultPM name;
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
    private Collection<Locale> modelLocales;

    public OutputPM(Collection<Locale> locales, Collection<Locale> modelLocales) {
        key = new TextPM();
        key.setMandatory(true);
        name = new LocalizedStringDefaultPM();
        templates = new EnumeratedValuesPM<>();
        templates.addPropertyChangeListener("text", evt -> {
            OutputTemplate template = templates.getValue();
            if (template != null) {
                LocalizedString lName = template.getName();
                if (lName == null) {
                    lName = LocalizedString.fromString(template.getKey());
                }
                name.init(lName, modelLocales);
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
        this.modelLocales = modelLocales;
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
        name.init(null, null);
        List<Wrapper<OutputTemplate>> wrappers = new ArrayList<>();
        for (OutputTemplate template : diagram.getOutputTemplates()) {
            wrappers.add(Wrapper.getWrapper(template));
        }
        Collections.sort(wrappers);
        for (Wrapper<OutputTemplate> wrapper : wrappers) {
            templates.addValue(wrapper.getElement(), wrapper.toString());
        }
        if (!wrappers.isEmpty()) {
            templates.setValue(wrappers.get(0).getElement());
        }
        locale.setValue(null);
    }

    public void init(TrainDiagram diagram, Output output) {
        diagramRef = new WeakReference<>(diagram);
        outputRef = new WeakReference<>(output);
        templates.getOptions().clear();
        name.init(output.getName(), modelLocales);
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
            newOutput.setName(name.getCurrentEdit().get());
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
            output.setName(name.getCurrentEdit().get());
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
