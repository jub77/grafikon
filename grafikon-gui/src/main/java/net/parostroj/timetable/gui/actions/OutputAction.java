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
import net.parostroj.timetable.output.NodeTimetablesList;
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

    public OutputAction(ApplicationModel model, Component parent) {
        this.model = model;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("stations"))
            this.stations();
        else if (e.getActionCommand().equals("stations_select"))
            this.stationsSelect();
    }

    private Frame getFrame() {
        if (parent instanceof Frame)
            return (Frame)parent;
        else
            return null;
    }

    private void stationsSelect() {
        ElementSelectionDialog<Node> selDialog = new ElementSelectionDialog<Node>(getFrame(), true);
        selDialog.setLocationRelativeTo(parent);
        List<Node> selection = selDialog.selectElements(new ArrayList<Node>(model.getDiagram().getNet().getNodes()));
        if (selection != null) {
            final NodeTimetablesList list = new NodeTimetablesList(selection, model.getDiagram());
            this.stationsImpl(list);
        }

    }

    private void stations() {
        NodeTimetablesList list = new NodeTimetablesList(model.getDiagram().getNet().getNodes(), model.getDiagram());
        this.stationsImpl(list);
    }

    private void stationsImpl(final NodeTimetablesList list) {
        HtmlAction action = new HtmlAction() {
                @Override
                public void write(Writer writer) throws Exception {
                    list.writeTo(writer);
                }

                @Override
                public void writeToDirectory(File directory) throws Exception {
                    // do nothing
                }
        };
        this.saveHtml(action);
    }

    private void saveHtml(final HtmlAction action) {
        final JFileChooser outputFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.OUTPUT);
        int retVal = outputFileChooser.showSaveDialog(parent);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.genoutput"), new ModelAction() {
                private String errorMessage;

                @Override
                public void run() {
                    try {
                        Writer writer = new OutputStreamWriter(new FileOutputStream(outputFileChooser.getSelectedFile()), "utf-8");
                        action.write(writer);
                        writer.close();
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
