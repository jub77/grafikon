package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.dialogs.Import;
import net.parostroj.timetable.gui.dialogs.ImportComponents;
import net.parostroj.timetable.gui.dialogs.ImportDialog;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.LSFileFactory;
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

        ActionContext context = new ActionContext(parent);
        ActionHandler handler = ActionHandler.getInstance();
        
        if (retVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = xmlFileChooser.getSelectedFile();
            ModelAction loadAction = new EventDispatchAfterModelAction(context) {

                private String errorMessage;

                @Override
                protected void backgroundAction() {
                    setWaitMessage(ResourceLoader.getString("wait.message.loadmodel"));
                    setWaitDialogVisible(true);
                    long time = System.currentTimeMillis();
                    try {
                        try {
                            FileLoadSave ls = LSFileFactory.getInstance().createForLoad(selectedFile);
                            context.setAttribute("diagram", ls.load(selectedFile));
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
                    } finally {
                        LOG.debug("Library loaded in {}ms", System.currentTimeMillis() - time);
                        setWaitDialogVisible(false);
                    }
                }
                
                @Override
                protected void eventDispatchActionAfter() {
                    if (errorMessage != null) {
                        String text = errorMessage + " " + xmlFileChooser.getSelectedFile().getName();
                        ActionUtils.showError(text, parent);
                    }
                }
            };
            handler.execute(loadAction);
        } else {
            // skip the rest
            return;
        }
        
        handler.execute(new EventDispatchModelAction(context) {
            
            @Override
            protected void eventDispatchAction() {
                TrainDiagram diagram = (TrainDiagram) context.getAttribute("diagram");
                if (diagram != null) {
                    importDialog.setTrainDiagrams(model.getDiagram(), diagram);
                    importDialog.setLocationRelativeTo(parent);
                    importDialog.setVisible(true);
                }
            }
        });

        ModelAction importAction = new EventDispatchAfterModelAction(context) {
            
            private static final int CHUNK_SIZE = 10;
            private Map<ImportComponents, Import> imports = new EnumMap<ImportComponents, Import>(ImportComponents.class);
            private boolean trainType;
            private int size;

            @Override
            protected void backgroundAction() {
                if (!importDialog.isSelected())
                    return;
                setWaitMessage(ResourceLoader.getString("wait.message.import"));
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
                try {
                    Map<ImportComponents, Set<ObjectWithId>> map = importDialog.getSelectedItems();
                    List<ObjectWithId> list = new LinkedList<ObjectWithId>();
                    for (ImportComponents comp : ImportComponents.values()) {
                        Set<ObjectWithId> set = map.get(comp);
                        list.addAll(set);
                        imports.put(comp, Import.getInstance(comp, importDialog.getDiagram(),
                                importDialog.getLibraryDiagram(), importDialog.getImportMatch()));
                    }
                    size = list.size();
                    if (size == 0)
                        return;
                    int totalCount = (size - 1) / CHUNK_SIZE + 1;
                    CountDownLatch signal = new CountDownLatch(totalCount);
                    List<ObjectWithId> batch = new LinkedList<ObjectWithId>();
                    Iterator<ObjectWithId> iterator = list.iterator();
                    int cnt = 0;
                    while (iterator.hasNext()) {
                        ObjectWithId o = iterator.next();
                        batch.add(o);
                        if (++cnt == CHUNK_SIZE) {
                            processChunk(batch, signal);
                            cnt = 0;
                            batch = new LinkedList<ObjectWithId>();
                        }
                    }
                    if (batch.size() > 0) {
                        processChunk(batch, signal);
                    }
                    try {
                        signal.await();
                    } catch (InterruptedException e) {
                        LOG.error("Recalculate - await interrupted.", e);
                    }
                } finally {
                    LOG.debug("Import finished in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            private void processChunk(final Collection<ObjectWithId> objects, final CountDownLatch signal) {
                ModelActionUtilities.runLaterInEDT(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (ObjectWithId o : objects) {
                                Import i = imports.get(ImportComponents.getByComponentClass(o.getClass()));
                                if (i != null) {
                                    if (o instanceof TrainType)
                                        trainType = true;
                                    ObjectWithId imported = i.importObject(o);
                                    processImportedObject(imported);
                                } else {
                                    LOG.warn("No import for class {}", o.getClass().getName());
                                }
                            }
                        } finally {
                            signal.countDown();
                        }
                    }
                });
            }
            
            @Override
            protected void eventDispatchActionAfter() {
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
                
                if (size > 0)
                    model.setModelChanged(true);
                
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
        };
        handler.execute(importAction);
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
