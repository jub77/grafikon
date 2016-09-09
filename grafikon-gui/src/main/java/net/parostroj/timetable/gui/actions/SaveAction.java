package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.actions.impl.CloseableFileChooser;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.ModelUtils;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Save/Save as action.
 *
 * @author jub
 */
public class SaveAction extends AbstractAction {

    private static final Logger log = LoggerFactory.getLogger(SaveAction.class);
    private final ApplicationModel model;

    public SaveAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component parent = GuiComponentUtils.getTopLevelComponent(e.getSource());
        if (e.getActionCommand().equals("save")) {
            this.save(parent);
        } else if (e.getActionCommand().equals("save_as")) {
            this.saveAs(parent);
        }
    }

    private void save(Component parent) {
        if (model.getOpenedFile() == null) {
            this.saveAs(parent);
            return;
        }
        if (model.getDiagram() == null) {
            GuiComponentUtils.showError(ResourceLoader.getString("dialog.error.nodiagram"), parent);
            return;
        }
        ActionContext c = new ActionContext(parent);
        ModelAction action = getSaveModelAction(c, model.getOpenedFile(), parent, model);
        ActionHandler.getInstance().execute(action);
    }

    private void saveAs(Component parent) {
        if (model.getDiagram() == null) {
            GuiComponentUtils.showError(ResourceLoader.getString("dialog.error.nodiagram"), parent);
            return;
        }
        // saving train diagram
        try (CloseableFileChooser gtmFileChooser = FileChooserFactory.getInstance()
                .getFileChooser(FileChooserFactory.Type.GTM)) {
            int retVal = gtmFileChooser.showSaveDialog(parent);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                model.setOpenedFile(gtmFileChooser.getSelectedFile());
                ActionContext c = new ActionContext(parent);
                ModelAction action = getSaveModelAction(c, gtmFileChooser.getSelectedFile(), parent, model);
                ActionHandler.getInstance().execute(action);
            }
        }
    }

    public static ModelAction getSaveModelAction(ActionContext context, final File file, final Component parent, final ApplicationModel model) {
        ModelAction action = new EventDispatchAfterModelAction(context) {

            private String errorMessage;

            @Override
            protected void backgroundAction() {
                setWaitMessage(ResourceLoader.getString("wait.message.savemodel"));
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
                try {
                    ModelUtils.saveModelData(model, file);
                } catch (LSException e) {
                    log.warn("Error saving model.", e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                } catch (Exception e) {
                    log.warn("Error saving model.", e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                } finally {
                    log.debug("Saved in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            @Override
            protected void eventDispatchActionAfter() {
                if (errorMessage != null) {
                    GuiComponentUtils.showError(errorMessage + " " + file.getName(), parent);
                } else {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODEL_SAVED, model));
                }
            }
        };

        return action;
    }
}
