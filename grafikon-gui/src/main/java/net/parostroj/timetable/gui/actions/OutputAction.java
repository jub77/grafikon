package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.dialogs.ElementSelectionDialog;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.gui.utils.ModelAction;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Output action.
 *
 * @author jub
 */
public class OutputAction extends AbstractAction {

    private static final String FACTORY = "groovy";
    private static final Logger LOG = Logger.getLogger(OutputAction.class.getName());
    private ApplicationModel model;
    private Component parent;

    public OutputAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parent = ActionUtils.getTopLevelComponent(e.getSource());
        try {
            if (e.getActionCommand().equals("stations"))
                this.stations();
            else if (e.getActionCommand().equals("stations_select"))
                this.stationsSelect();
            else if (e.getActionCommand().equals("ends"))
                this.endPositions();
            else if (e.getActionCommand().equals("starts"))
                this.startPositions();
        } catch (OutputException ex) {
            String errorMessage = ResourceLoader.getString("dialog.error.saving");
            showError(errorMessage + ": " + ex.getMessage(), parent);
        }
    }

    private Frame getFrame() {
        if (parent instanceof Frame)
            return (Frame)parent;
        else
            return null;
    }

    private void stationsSelect() throws OutputException {
        ElementSelectionDialog<Node> selDialog = new ElementSelectionDialog<Node>(getFrame(), true);
        selDialog.setLocationRelativeTo(parent);
        List<Node> selection = selDialog.selectElements(new ArrayList<Node>(model.getDiagram().getNet().getNodes()));
        if (selection != null) {
            Output output = this.createOutput("stations");
            OutputParams params = this.createParams(output);
            params.setParam("stations", selection);
            this.singleHtmlOutputImpl(output, params);
        }
    }

    private void stations() throws OutputException {
        Output output = this.createOutput("stations");
        this.singleHtmlOutputImpl(output, this.createParams(output));
    }

    private void endPositions() throws OutputException {
        Output output = this.createOutput("ends");
        this.singleHtmlOutputImpl(output, this.createParams(output));
    }

    private void startPositions() throws OutputException {
        Output output = this.createOutput("starts");
        this.singleHtmlOutputImpl(output, this.createParams(output));
    }

    private Output createOutput(String outputType) throws OutputException {
        OutputFactory of = OutputFactory.newInstance(FACTORY);
        Output output = of.createOutput(outputType);
        return output;
    }

    private OutputParams createParams(Output output) {
        OutputParams params = output.getAvailableParams();
        params.getParam(DefaultOutputParam.TRAIN_DIAGRAM).setValue(model.getDiagram());
        return params;
    }

    private void singleHtmlOutputImpl(final Output output, final OutputParams params) {
        HtmlOutputAction action = new HtmlOutputAction() {
                @Override
                public void write(OutputStream stream) throws Exception {
                    params.getParam(DefaultOutputParam.OUTPUT_STREAM).setValue(stream);
                    output.write(params);
                }

                @Override
                public void writeToDirectory(File directory) throws Exception {
                    // do nothing
                }
        };
        this.saveHtml(action);
    }

    private void saveHtml(final HtmlOutputAction action) {
        final JFileChooser outputFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.OUTPUT);
        int retVal = outputFileChooser.showSaveDialog(parent);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.genoutput"), new ModelAction() {
                private String errorMessage;

                @Override
                public void run() {
                    try {
                        FileOutputStream stream = new FileOutputStream(outputFileChooser.getSelectedFile());
                        action.write(stream);
                        stream.close();
                        action.writeToDirectory(outputFileChooser.getSelectedFile().getParentFile());
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
                    if (errorMessage != null)
                        showError(errorMessage + " " + outputFileChooser.getSelectedFile().getName(), parent);
                }
            });
        }
    }

    private void showError(String text, Component parent) {
        JOptionPane.showMessageDialog(parent, text, ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
    }
}
