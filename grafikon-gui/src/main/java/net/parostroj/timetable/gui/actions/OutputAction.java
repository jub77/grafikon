package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
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
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.output.ImageSaver;
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
    private String actionCommand;
    private String outputType;

    public OutputAction(ApplicationModel model, Frame frame) {
        this.model = model;
        this.templateSelectDialog = new TemplateSelectDialog(frame, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parent = ActionUtils.getTopLevelComponent(e.getSource());
        outputFile = null;
        templateFile = null;
        actionCommand = e.getActionCommand();
        if (!selectTemplate())
            return;
        if (!selectOutput())
            return;
        setOutputType();
        try {
            if (actionCommand.equals("stations_select")) {
                this.stationsSelect();
            } else if (outputType != null) {
                this.singleOutput();
            }
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
                    null)) {
                templateFile = templateSelectDialog.getTemplate();
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

    private void setOutputType() {
        outputType = null;
        if (actionCommand.equals("stations")) {
            outputType = "stations";
        } else if (actionCommand.equals("stations_select")) {
            outputType = "stations";
        } else if (actionCommand.equals("ends")) {
            outputType = "ends";
        } else if (actionCommand.equals("starts")) {
            outputType = "starts";
        }
    }

    private Frame getFrame() {
        if (parent instanceof Frame) {
            return (Frame) parent;
        } else {
            return null;
        }
    }

    private void stationsSelect() throws OutputException {
        ElementSelectionDialog<Node> selDialog = new ElementSelectionDialog<Node>(getFrame(), true);
        selDialog.setLocationRelativeTo(parent);
        List<Node> selection = selDialog.selectElements(new ArrayList<Node>(model.getDiagram().getNet().getNodes()));
        if (selection != null) {
            Output output = this.createOutput();
            OutputParams params = this.createParams(output);
            params.setParam("stations", selection);
            this.saveHtml(this.createSingleHtmlOutputImpl(
                    new ExecutableOutput(output, this.createParams(output)),
                    false, false));
        }
    }

    private void singleOutput() throws OutputException {
        Output output = this.createOutput();
        this.saveHtml(this.createSingleHtmlOutputImpl(
                new ExecutableOutput(output, this.createParams(output)),
                false, false));
    }

    private Output createOutput() throws OutputException {
        OutputFactory of = OutputFactory.newInstance(model.getOutputCategory().getOutputFactoryType());
        Output output = of.createOutput(outputType);
        return output;
    }

    private OutputParams createParams(Output output) {
        OutputParams params = output.getAvailableParams();
        params.setParam(DefaultOutputParam.TRAIN_DIAGRAM, model.getDiagram());
        return params;
    }

    private HtmlOutputAction createSingleHtmlOutputImpl(final ExecutableOutput output, final boolean saveImages, final boolean saveTDImages) {
        HtmlOutputAction action = new HtmlOutputAction() {

            @Override
            public void write(OutputStream stream) throws Exception {
                output.getParams().setParam(DefaultOutputParam.OUTPUT_STREAM, stream);
                output.execute();
            }

            @Override
            public void writeToDirectory(File directory) throws Exception {
                if (saveImages) {
                    new ImageSaver().saveTrainTimetableImages(directory);
                }
                if (saveTDImages) // TODO missing implementation
                ;
            }
        };
        return action;
    }

    private void saveHtml(final HtmlOutputAction action) {
        this.saveHtml(Collections.singletonList(action), false);
    }

    private void saveHtml(final List<HtmlOutputAction> actions, final boolean directory) {
        ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.genoutput"), new ModelAction() {

            private String errorMessage;

            @Override
            public void run() {
                try {
                    for (HtmlOutputAction action : actions) {
                        if (!directory) {
                            FileOutputStream stream = new FileOutputStream(outputFile);
                            action.write(stream);
                            stream.close();
                            action.writeToDirectory(outputFile.getParentFile());
                        } else {
                            action.writeToDirectory(outputFile);
                        }
                    }
                } catch (IOException e) {
                    LOG.log(Level.WARNING, e.getMessage(), e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                } catch (Exception e) {
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
