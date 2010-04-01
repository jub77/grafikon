package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.gui.utils.ModelAction;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Combined new/open/save action.
 *
 * @author jub
 */
public class NewOpenSaveAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(NewOpenSaveAction.class.getName());
    private ApplicationModel model;
    private Component parent;

    public NewOpenSaveAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parent = ActionUtils.getTopLevelComponent(e.getSource());
        if (e.getActionCommand().equals("open"))
            this.open();
    }

    private void open() {
        // check changes
        final int result = ModelUtils.checkModelChangedContinue(model, parent);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }
        // loading train diagram
        final JFileChooser xmlFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.XML);
        final int retVal = xmlFileChooser.showOpenDialog(parent);
        ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.loadmodel"), new ModelAction() {

            private TrainDiagram diagram;
            private String errorMessage;
            private String errorSaveMessage;
            private Exception errorException;

            @Override
            public void run() {
                try {
                    if (result == JOptionPane.YES_OPTION) {
                        ModelUtils.saveModelData(model, model.getOpenedFile());
                    }
                    try {
                        if (retVal == JFileChooser.APPROVE_OPTION) {
                            model.setOpenedFile(xmlFileChooser.getSelectedFile());
                            FileLoadSave ls = LSFileFactory.getInstance().createForLoad(xmlFileChooser.getSelectedFile());
                            diagram = ls.load(xmlFileChooser.getSelectedFile());
                        }
                    } catch (LSException e) {
                        LOG.log(Level.WARNING, "Error loading model.", e);
                        if (e.getCause() instanceof FileNotFoundException) {
                            errorMessage = ResourceLoader.getString("dialog.error.filenotfound");
                        } else if (e.getCause() instanceof IOException) {
                            errorMessage = ResourceLoader.getString("dialog.error.loading");
                        } else {
                            errorMessage = ResourceLoader.getString("dialog.error.loading");
                            errorException = e;
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Error loading model.", e);
                        errorMessage = ResourceLoader.getString("dialog.error.loading");
                    }
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Error saving model.", e);
                    errorSaveMessage = ResourceLoader.getString("dialog.error.saving");
                }
            }

            @Override
            public void afterRun() {
                if (errorSaveMessage != null) {
                    ActionUtils.showError(errorSaveMessage, parent);
                    return;
                }
                if (result == JOptionPane.YES_OPTION) {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODEL_SAVED, model));
                }
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    if (diagram != null) {
                        model.setDiagram(diagram);
                    } else {
                        String text = errorMessage + " " + xmlFileChooser.getSelectedFile().getName();
                        if (errorException != null) {
                            text = text + "\n(" + errorException.getMessage() + ")";
                        }
                        ActionUtils.showError(text, parent);
                        model.setDiagram(null);
                    }
                }
            }
        });
    }
}
