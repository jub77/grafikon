package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.dialogs.Import;
import net.parostroj.timetable.gui.dialogs.ImportComponents;
import net.parostroj.timetable.gui.dialogs.ImportDialog;
import net.parostroj.timetable.gui.modelactions.ActionHandler;
import net.parostroj.timetable.gui.modelactions.EDTModelAction;
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
                    ResourceLoader.getString("wait.message.loadmodel"), new ModelAction("Library load") {
                
                private String errorMessage;
                
                @Override
                public void run() {
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
                    ActionUtils.runInEDT(new Runnable() {
                        
                        @Override
                        public void run() {
                            if (errorMessage != null) {
                                String text = errorMessage + " " + xmlFileChooser.getSelectedFile().getName();
                                ActionUtils.showError(text, parent);
                            }
                        }
                    });
                }
            });
        } else {
            // skip the rest
            return;
        }

        handler.executeActionWithoutDialog(new Runnable() {

            @Override
            public void run() {
                if (diagram.get() != null)
                    ActionUtils.runInEDT(new Runnable() {

                        @Override
                        public void run() {
                            importDialog.setTrainDiagrams(model.getDiagram(), diagram.get());
                            importDialog.setLocationRelativeTo(parent);
                            importDialog.setVisible(true);
                        }
                    });
            }

        });

        handler.executeAction(parent, ResourceLoader.getString("wait.message.import"), new EDTModelAction<ObjectWithId>("Import") {
            
            private static final int CHUNK_SIZE = 10;
            private boolean trainType;
            private Iterator<ObjectWithId> objects = null;
            private Map<ImportComponents, Import> imports = new EnumMap<ImportComponents, Import>(ImportComponents.class);

            @Override
            protected boolean prepareItems() throws Exception {
                if (!importDialog.isSelected())
                    return false;
                if (objects == null) {
                    Map<ImportComponents, Set<ObjectWithId>> map = importDialog.getSelectedItems();
                    List<ObjectWithId> list = new LinkedList<ObjectWithId>();
                    for (ImportComponents comp : ImportComponents.values()) {
                        Set<ObjectWithId> set = map.get(comp);
                        list.addAll(set);
                        imports.put(comp, Import.getInstance(comp, importDialog.getDiagram(),
                                importDialog.getLibraryDiagram(), importDialog.getImportMatch()));
                    }
                    objects = list.iterator();
                }
                int cnt = 0;
                while (objects.hasNext() && cnt++ < CHUNK_SIZE) {
                    addItems(objects.next());
                }
                return objects.hasNext();
            }
            
            @Override
            protected void processItem(ObjectWithId item) throws Exception {
                Import i = imports.get(ImportComponents.getByComponentClass(item.getClass()));
                if (i != null) {
                    if (item instanceof TrainType)
                        trainType = true;
                    ObjectWithId imported = i.importObject(item);
                    processImportedObject(imported);
                } else {
                    LOG.warn("No import for class {}", item.getClass().getName());
                }
            }
            
            @Override
            protected void itemsFinished() {
                boolean selected = importDialog.isSelected();
                importDialog.clear();
                if (!selected)
                    return;
                if (trainType) {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.TRAIN_TYPES_CHANGED, model));
                }
                List<Object> errors = new LinkedList<Object>();
                for (ImportComponents comp : ImportComponents.values()) {
                    Import i = imports.get(comp);
                    errors.addAll(i.getErrors());
                }
                // create string ...
                if (!errors.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    int lineLength = 70;
                    int nextLimit = lineLength;
                    for (Object error : errors) {
                        if (message.length() != 0) {
                            message.append(", ");
                        }
                        if (nextLimit < message.length()) {
                            message.append('\n');
                            nextLimit += lineLength;
                        }
                        message.append(getText(error));
                    }
                    JOptionPane.showConfirmDialog(parent, message,
                            ResourceLoader.getString("import.warning.title"),
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void processImportedObject(ObjectWithId o) {
        // process new trains
        if (o instanceof Train) {
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_TRAIN, model, o));
        } else if (o instanceof Node) {
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_NODE, model, o));
        }
    }

    private String getText(Object oid) {
        if (oid instanceof Train) {
            return ((Train) oid).getName();
        } else if (oid instanceof Node) {
            return ((Node) oid).getName();
        } else if (oid instanceof TrainType) {
            return ((TrainType) oid).getDesc();
        } else {
            return oid.toString();
        }
    }
}
