package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.dialogs.NewModelDialog;
import net.parostroj.timetable.gui.modelactions.ActionHandler;
import net.parostroj.timetable.gui.modelactions.ModelAction;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Combined new/open action.
 *
 * @author jub
 */
public class NewOpenAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(NewOpenAction.class.getName());
    private ApplicationModel model;
    private NewModelDialog newModelDialog;

    /**
     * creates a new instance
     *
     * @param model application model
     * @param owner frame
     */
    public NewOpenAction(ApplicationModel model, Frame owner, boolean createNewDialog) {
        this.model = model;
        if (createNewDialog) {
            newModelDialog = new NewModelDialog(owner, true);
            newModelDialog.setModel(model);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component parent = ActionUtils.getTopLevelComponent(e.getSource());
        if (e.getActionCommand().equals("open")) {
            this.open(parent);
        } else if (e.getActionCommand().equals("new")) {
            this.create(parent);
        }
    }

    private void open(final Component parent) {
        // check changes
        final int result = ModelUtils.checkModelChangedContinue(model, parent);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }
        // loading train diagram
        final JFileChooser xmlFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.GTM);
        final int retVal = xmlFileChooser.showOpenDialog(parent);
        if (result == JOptionPane.YES_OPTION) {
            SaveAction.saveModel(model.getOpenedFile(), parent, model);
        }
        if (retVal != JFileChooser.APPROVE_OPTION)
            // cancel
            return;
        ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.loadmodel"), new ModelAction("Open") {

            private TrainDiagram diagram;
            private String errorMessage;
            private String errorSaveMessage;
            private Exception errorException;

            @Override
            public void run() {
                try {
                    try {
                        model.setOpenedFile(xmlFileChooser.getSelectedFile());
                        FileLoadSave ls = LSFileFactory.getInstance().createForLoad(xmlFileChooser.getSelectedFile());
                        diagram = ls.load(xmlFileChooser.getSelectedFile());
                    } catch (LSException e) {
                        LOG.warn("Error loading model.", e);
                        if (e.getCause() instanceof FileNotFoundException) {
                            errorMessage = ResourceLoader.getString("dialog.error.filenotfound");
                        } else if (e.getCause() instanceof IOException) {
                            errorMessage = ResourceLoader.getString("dialog.error.loading");
                        } else {
                            errorMessage = ResourceLoader.getString("dialog.error.loading");
                            errorException = e;
                        }
                    } catch (Exception e) {
                        LOG.warn("Error loading model.", e);
                        errorMessage = ResourceLoader.getString("dialog.error.loading");
                    }
                } catch (Exception e) {
                    LOG.warn("Error saving model.", e);
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

    private void create(final Component parent) {
        // check changes
        final int result = ModelUtils.checkModelChangedContinue(model, parent);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }
        ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.newmodel"), new ModelAction("New model") {

            private String errorMessage;

            @Override
            public void run() {
                try {
                    if (result == JOptionPane.YES_OPTION) {
                        ModelUtils.saveModelData(model, model.getOpenedFile());
                    }
                } catch (Exception e) {
                    LOG.warn("Error saving model.", e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                }
            }

            @Override
            public void afterRun() {
                if (errorMessage != null) {
                    ActionUtils.showError(errorMessage, parent);
                    return;
                }
                if (result == JOptionPane.YES_OPTION) {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODEL_SAVED, model));
                }
                // create new model
                newModelDialog.setLocationRelativeTo(parent);
                newModelDialog.setVisible(true);
            }
        });

    }
}
