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
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.dialogs.NewModelDialog;
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

        // save old diagram
        ActionContext context = new ActionContext(parent);
        if (result == JOptionPane.YES_OPTION) {
            ModelAction saveAction = SaveAction.getSaveModelAction(context, model.getOpenedFile(), parent, model);
            ActionHandler.getInstance().execute(saveAction);
        }
        
        ModelAction openAction = new CombinedModelAction(context) {

            private JFileChooser xmlFileChooser;
            private int retVal;
            private TrainDiagram diagram;
            private String errorMessage;
            private Exception errorException;
            
            @Override
            protected void eventDispatchActionBefore() {
                xmlFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.GTM);
                retVal = xmlFileChooser.showOpenDialog(parent);
            }
            
            @Override
            protected void backgroundAction() {
                if (retVal != JFileChooser.APPROVE_OPTION)
                    return;
                setWaitMessage(ResourceLoader.getString("wait.message.loadmodel"));
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
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
                } finally {
                    LOG.debug("Loaded in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }
            
            @Override
            protected void eventDispatchActionAfter() {
                if (retVal != JFileChooser.APPROVE_OPTION)
                    return;
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
        };
        ActionHandler.getInstance().execute(openAction);
    }

    private void create(final Component parent) {
        // check changes
        final int result = ModelUtils.checkModelChangedContinue(model, parent);
        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }
        
        // save old diagram
        ActionContext context = new ActionContext(parent);
        if (result == JOptionPane.YES_OPTION) {
            ModelAction saveAction = SaveAction.getSaveModelAction(context, model.getOpenedFile(), parent, model);
            ActionHandler.getInstance().execute(saveAction);
        }
        
        // new
        ModelAction newAction = new EventDispatchModelAction(context) {
            
            @Override
            protected void eventDispatchAction() {
                // create new model
                newModelDialog.setLocationRelativeTo(parent);
                newModelDialog.setVisible(true);
            }
        };
        ActionHandler.getInstance().execute(newAction);
    }
}
