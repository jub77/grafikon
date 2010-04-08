package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.dialogs.ImportDialog;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Import action.
 *
 * @author jub
 */
public class ImportAction extends AbstractAction {

    private static final Logger LOG = Logger.getLogger(ImportAction.class.getName());
    private ImportDialog importDialog;
    private ApplicationModel model;

    public ImportAction(ApplicationModel model, Frame frame) {
        this.model = model;
        this.importDialog = new ImportDialog(frame, true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Component parent = ActionUtils.getTopLevelComponent(event.getSource());
        // select imported model
        final JFileChooser xmlFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.GTM);
        final int retVal = xmlFileChooser.showOpenDialog(parent);
        String errorMessage = null;
        TrainDiagram diagram = null;

        try {
            if (retVal == JFileChooser.APPROVE_OPTION) {
                FileLoadSave ls = LSFileFactory.getInstance().createForLoad(xmlFileChooser.getSelectedFile());
                diagram = ls.load(xmlFileChooser.getSelectedFile());
            } else {
                // skip the rest
                return;
            }
        } catch (LSException e) {
            LOG.log(Level.WARNING, "Error loading model.", e);
            if (e.getCause() instanceof FileNotFoundException)
                errorMessage = ResourceLoader.getString("dialog.error.filenotfound");
            else
                errorMessage = ResourceLoader.getString("dialog.error.loading");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Error loading model.", e);
            errorMessage = ResourceLoader.getString("dialog.error.loading");
        }

        if (errorMessage != null) {
            String text = errorMessage + " " + xmlFileChooser.getSelectedFile().getName();
            ActionUtils.showError(text, parent);
            return;
        }

        importDialog.setTrainDiagrams(model.getDiagram(), diagram);
        importDialog.setLocationRelativeTo(parent);
        importDialog.setVisible(true);

        this.processImportedObjects(importDialog.getImportedObjects());
    }

    private void processImportedObjects(Set<ObjectWithId> objects) {
        boolean trainTypesEvent = false;
        for (ObjectWithId o : objects) {
            // process new trains
            if (o instanceof Train) {
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_TRAIN, model, o));
            } else if (o instanceof Node) {
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_NODE, model, o));
            } else if (o instanceof TrainType) {
                trainTypesEvent = true;
            }
        }
        if (trainTypesEvent) {
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.TRAIN_TYPES_CHANGED, model));
        }
    }
}
