package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.utils.ActionHandler;
import net.parostroj.timetable.gui.utils.ModelAction;
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

    private static final Logger LOG = LoggerFactory.getLogger(SaveAction.class.getName());
    private ApplicationModel model;

    public SaveAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component parent = ActionUtils.getTopLevelComponent(e.getSource());
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
            ActionUtils.showError(ResourceLoader.getString("dialog.error.nodiagram"), parent);
            return;
        }
        this.saveModel(model.getOpenedFile(), parent);
    }

    private void saveAs(Component parent) {
        if (model.getDiagram() == null) {
            ActionUtils.showError(ResourceLoader.getString("dialog.error.nodiagram"), parent);
            return;
        }
        // saving train diagram
        JFileChooser xmlFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.GTM);
        int retVal = xmlFileChooser.showSaveDialog(parent);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            model.setOpenedFile(xmlFileChooser.getSelectedFile());
            this.saveModel(xmlFileChooser.getSelectedFile(), parent);
        }
    }

    private void saveModel(final File file, final Component parent) {
        if (model.getDiagram() == null) {
            ActionUtils.showError(ResourceLoader.getString("dialog.error.nodiagram"), parent);
            return;
        }

        ActionHandler.getInstance().executeAction(parent, ResourceLoader.getString("wait.message.savemodel"), new ModelAction() {
            private String errorMessage = null;

            @Override
            public void run() {
                try {
                    ModelUtils.saveModelData(model, file);
                } catch (LSException e) {
                    LOG.warn("Error saving model.", e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                } catch (Exception e) {
                    LOG.warn("Error saving model.", e);
                    errorMessage = ResourceLoader.getString("dialog.error.saving");
                }
            }

            @Override
            public void afterRun() {
                if (errorMessage != null) {
                    ActionUtils.showError(errorMessage + " " + file.getName(), parent);
                } else {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODEL_SAVED, model));
                }
            }
        });
    }
}
