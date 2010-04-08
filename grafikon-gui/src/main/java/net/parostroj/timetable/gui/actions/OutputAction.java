package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.dialogs.ElementSelectionDialog;
import net.parostroj.timetable.gui.dialogs.TemplateSelectDialog;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.gui.utils.ModelAction;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Output action.
 *
 * @author jub
 */
public class OutputAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(OutputAction.class.getName());
    private ApplicationModel model;
    private Component parent;
    private TemplateSelectDialog templateSelectDialog;

    // selection variables
    private File outputFile;
    private File templateFile;
    private OutputType outputType;
    private Object selection;

    public OutputAction(ApplicationModel model, Frame frame) {
        this.model = model;
        this.templateSelectDialog = new TemplateSelectDialog(frame, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parent = ActionUtils.getTopLevelComponent(e.getSource());
        outputFile = null;
        templateFile = null;
        outputType = OutputType.fromActionCommand(e.getActionCommand());
        selection = null;

        if (!makeSelection())
            return;
        if (!selectTemplate())
            return;
        if (!selectOutput())
            return;
        try {
            this.singleOutput();
        } catch (OutputException ex) {
            String errorMessage = ResourceLoader.getString("dialog.error.saving");
            ActionUtils.showError(errorMessage + ": " + ex.getMessage(), parent);
        }
    }

    private boolean selectTemplate() {
        OutputCategory category = model.getOutputCategory();
        if (category.isTemplateSelect()) {
            if (templateSelectDialog.selectTemplate(
                    FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.TEMPLATE),
                    model.getOutputTemplates().get(outputType.getOutputType()))) {
                templateFile = templateSelectDialog.getTemplate();
                if (templateFile != null && templateFile.canRead()) {
                    model.getOutputTemplates().put(outputType.getOutputType(), templateFile);
                }
            }
            return templateFile != null && templateFile.canRead();
        }
        return true;
    }

    private boolean selectOutput() {
        OutputCategory category = model.getOutputCategory();
        JFileChooser chooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.OUTPUT, category.getSuffix(), ResourceLoader.getString("output." + category.getSuffix()));
        int retVal = chooser.showSaveDialog(parent);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            outputFile = chooser.getSelectedFile();
            return outputFile != null;
        } else {
            return false;
        }
    }

    private Frame getFrame() {
        if (parent instanceof Frame) {
            return (Frame) parent;
        } else {
            return null;
        }
    }

    private boolean makeSelection() {
        if (outputType.isSelection()) {
            ElementSelectionDialog<Object> selDialog = new ElementSelectionDialog<Object>(getFrame(), true);
            selDialog.setLocationRelativeTo(parent);
            selection = selDialog.selectElements((List<Object>)ModelUtils.selectAllElements(model.getDiagram(), outputType.getSelectionClass()));
            if (selection == null)
                return false;
        }
        return true;
    }

    private void singleOutput() throws OutputException {
        Output output = this.createOutput();
        OutputParams params = this.createParams(output, outputFile);
        this.saveOutputs(Collections.singletonList(new ExecutableOutput(output, params)));
    }

    private Output createOutput() throws OutputException {
        OutputFactory of = OutputFactory.newInstance(model.getOutputCategory().getOutputFactoryType());
        Output output = of.createOutput(outputType.getOutputType());
        return output;
    }

    private OutputParams createParams(Output output, File file) throws OutputException {
        OutputParams params = output.getAvailableParams();
        // diagram
        params.setParam(DefaultOutputParam.TRAIN_DIAGRAM, model.getDiagram());
        // template
        if (templateFile != null) {
            try {
                params.setParam(DefaultOutputParam.TEMPLATE_STREAM, new FileInputStream(templateFile));
            } catch (FileNotFoundException e) {
                throw new OutputException(e);
            }
        }
        // selections
        if (outputType.isSelection()) {
            params.setParam(outputType.getSelectionParam(), selection);
        }
        try {
            params.setParam(DefaultOutputParam.OUTPUT_STREAM, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new OutputException(e);
        }
        return params;
    }

    private void saveOutputs(final Collection<ExecutableOutput> outputs) {
        ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.genoutput"), new ModelAction() {

            private String errorMessage;

            @Override
            public void run() {
                try {
                    for (ExecutableOutput output : outputs) {
                        output.execute();
                    }
                } catch (OutputException e) {
                    LOG.log(Level.WARNING, e.getMessage(), e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                }
            }

            @Override
            public void afterRun() {
                if (errorMessage != null) {
                    ActionUtils.showError(errorMessage + " " + outputFile.getName(), parent);
                }
            }
        });
    }
}
