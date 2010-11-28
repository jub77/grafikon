package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.dialogs.ImportDialog;
import net.parostroj.timetable.gui.modelactions.ActionHandler;
import net.parostroj.timetable.gui.modelactions.ModelAction;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
import net.parostroj.timetable.utils.ReferenceHolder;
import net.parostroj.timetable.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import action.
 *
 * @author jub
 */
public class ImportAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ImportAction.class.getName());
    private ImportDialog importDialog;
    private ApplicationModel model;

    public ImportAction(ApplicationModel model, Frame frame) {
        this.model = model;
        this.importDialog = new ImportDialog(frame, true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Component parent = ActionUtils.getTopLevelComponent(event.getSource());
        // select imported model
        final JFileChooser xmlFileChooser = FileChooserFactory.getInstance().getFileChooser(FileChooserFactory.Type.GTM);
        final int retVal = xmlFileChooser.showOpenDialog(parent);
        final ReferenceHolder<TrainDiagram> diagram = new ReferenceHolder<TrainDiagram>();

        ActionHandler handler = ActionHandler.getInstance();
        
        if (retVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = xmlFileChooser.getSelectedFile();
            handler.executeAction(parent,
                    ResourceLoader.getString("wait.message.loadmodel"), new ModelAction() {
                
                private String errorMessage;
                
                @Override
                public void run() {
                    LOG.debug("loading....");
                    try {
                        FileLoadSave ls = LSFileFactory.getInstance().createForLoad(selectedFile);
                        diagram.set(ls.load(selectedFile));
                    } catch (LSException e) {
                        LOG.warn("Error loading model.", e);
                        if (e.getCause() instanceof FileNotFoundException)
                            errorMessage = ResourceLoader.getString("dialog.error.filenotfound");
                        else
                            errorMessage = ResourceLoader.getString("dialog.error.loading");
                    } catch (Exception e) {
                        LOG.warn("Error loading model.", e);
                        errorMessage = ResourceLoader.getString("dialog.error.loading");
                    }
                }
                
                @Override
                public void afterRun() {
                    LOG.debug("AFTER load...");
                    if (errorMessage != null) {
                        String text = errorMessage + " " + xmlFileChooser.getSelectedFile().getName();
                        ActionUtils.showError(text, parent);
                    }
                }
            });
        } else {
            // skip the rest
            return;
        }

        handler.executeActionWithoutDialog(new Runnable() {
            
            @Override
            public void run() {
                LOG.debug("...runn .....");
                if (diagram.get() != null) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            
                            @Override
                            public void run() {
                                LOG.debug("IMPORT.dialog.");
                                importDialog.setTrainDiagrams(model.getDiagram(), diagram.get());
                                importDialog.setLocationRelativeTo(parent);
                                importDialog.setVisible(true);
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        this.processImportedObjects(importDialog.getImportedObjects());
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
