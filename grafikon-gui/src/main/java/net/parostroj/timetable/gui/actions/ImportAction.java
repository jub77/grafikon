package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.parostroj.timetable.filters.ModelPredicates;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.actions.impl.CloseableFileChooser;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.LoadDiagramModelAction;
import net.parostroj.timetable.gui.actions.impl.LoadLibraryModelAction;
import net.parostroj.timetable.gui.actions.impl.Process;
import net.parostroj.timetable.gui.commands.CommandException;
import net.parostroj.timetable.gui.commands.DeleteTrainCommand;
import net.parostroj.timetable.gui.components.ExportImportSelection;
import net.parostroj.timetable.gui.components.ExportImportSelectionSource;
import net.parostroj.timetable.gui.dialogs.ExportImportSelectionDialog;
import net.parostroj.timetable.gui.dialogs.GroupChooserFromToDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.imports.Import.ImportError;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.imports.TrainDiagramPartImport;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Import action.
 *
 * @author jub
 */
public class ImportAction extends AbstractAction {

    private final class TrainOidFilter implements Predicate<ObjectWithId> {
        private final Group group;

        private TrainOidFilter(Group group) {
            this.group = group;
        }

        @Override
        public boolean apply(ObjectWithId item) {
            if (item instanceof Train) {
                Group foundGroup = ((Train) item).getAttributes().get("group", Group.class);
                if (group == null) {
                    return foundGroup == null;
                } else {
                    return group.equals(foundGroup);
                }
            } else {
                return true;
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ImportAction.class);

    private final ExportImportSelectionDialog importDialog;
    private final GroupChooserFromToDialog groupDialog;
    private final ApplicationModel model;
    private final boolean trainImport;
    private final boolean supportLibrary;

    public ImportAction(ApplicationModel model, Frame frame, boolean trainImport, boolean supportLibrary) {
        this.model = model;
        this.trainImport = trainImport;
        this.supportLibrary = supportLibrary;
        this.importDialog = new ExportImportSelectionDialog(frame, true);
        this.groupDialog = trainImport ? new GroupChooserFromToDialog() : null;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Component parent = GuiComponentUtils.getTopLevelComponent(event.getSource());

        ActionContext context = new ActionContext(parent);
        ActionHandler handler = ActionHandler.getInstance();

        // select imported model
        try (CloseableFileChooser gtmFileChooser = FileChooserFactory.getInstance()
                .getFileChooser(supportLibrary ? FileChooserFactory.Type.GTM_GTML : FileChooserFactory.Type.GTM)) {
            final int retVal = gtmFileChooser.showOpenDialog(parent);


            if (retVal == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = gtmFileChooser.getSelectedFile();
                ModelAction loadAction = selectedFile.getName().endsWith(".gtm") ?
                        new LoadDiagramModelAction(context, selectedFile, parent) :
                            new LoadLibraryModelAction(context, selectedFile, parent);
                handler.execute(loadAction);
            } else {
                // skip the rest
                return;
            }
        }

        handler.execute(new EventDispatchModelAction(context) {

            @Override
            protected void eventDispatchAction() {
                TrainDiagram diagram = (TrainDiagram) context.getAttribute("diagram");
                Library library = (Library) context.getAttribute("library");
                boolean cancelled = false;
                Predicate<ObjectWithId> filter = null;
                if (trainImport) {
                    groupDialog.setLocationRelativeTo(parent);
                    groupDialog.showDialog(diagram, null, model.getDiagram(), null);
                    if (groupDialog.isSelected()) {
                        final Group group = groupDialog.getSelectedFrom();
                        filter = new TrainOidFilter(group);
                    } else {
                        cancelled = !groupDialog.isSelected();
                    }
                }
                if ((diagram != null || library != null) && !cancelled) {
                    ExportImportSelectionSource source;
                    if (trainImport) {
                        source = ExportImportSelectionSource.fromDiagramSingleTypeWithFilter(diagram, ImportComponent.TRAINS, filter::apply);
                    } else {
                        source = diagram != null ?
                                ExportImportSelectionSource.fromDiagramToDiagram(diagram) :
                                    ExportImportSelectionSource.fromLibraryToDiagram(library);
                    }
                    importDialog.setSelectionSource(source);
                    importDialog.setLocationRelativeTo(parent);
                    importDialog.setVisible(true);
                    cancelled = importDialog.isCancelled();
                }
                context.setAttribute("cancelled", cancelled);
            }
        });

        ModelAction importAction = new EventDispatchAfterModelAction(context) {

            private static final int CHUNK_SIZE = 10;
            private TrainDiagramPartImport imports;
            private int size;
            private final CyclicBarrier barrier = new CyclicBarrier(2);

            @Override
            protected void backgroundAction() {
                boolean cancelled = (Boolean) context.getAttribute("cancelled");
                if (cancelled) {
                    return;
                }
                setWaitMessage(ResourceLoader.getString("wait.message.import"));
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
                try {
                    ExportImportSelection selection = importDialog.getSelection();
                    Map<ImportComponent, Collection<ObjectWithId>> map = selection.getObjects();
                    imports = new TrainDiagramPartImport(model.getDiagram(), selection.getImportMatch(), selection.isImportOverwrite());
                    List<ObjectWithId> list = map.values().stream().sequential().flatMap(item -> item.stream().sequential()).collect(Collectors.toList());
                    size = list.size();
                    if (size == 0) {
                        return;
                    }
                    if (trainImport && groupDialog.isRemoveExistingTrains()) {
                        // remove existing trains in group
                        Process<ObjectWithId> deleteProcess = item -> {
                            DeleteTrainCommand dc = new DeleteTrainCommand((Train) item);
                            try {
                                model.applyCommand(dc);
                            } catch (CommandException e) {
                                log.error(e.getMessage(), e);
                            }
                        };
                        Iterable<Train> filteredTrains = Iterables.filter(model.getDiagram().getTrains(), ModelPredicates.inGroup(groupDialog.getSelectedTo()));
                        processItems(filteredTrains, deleteProcess);
                    }
                    // import new objects
                    Process<ObjectWithId> importProcess = item -> {
                        ImportComponent i = ImportComponent.getByComponentClass(item.getClass());
                        if (i != null) {
                            ObjectWithId imported = imports.importPart(item);
                            processImportedObject(imported);
                        } else {
                            log.warn("No import for class {}", item.getClass().getName());
                        }
                    };
                    processItems(list, importProcess);
                } finally {
                    log.debug("Import finished in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            private void processItems(Iterable<? extends ObjectWithId> list, Process<ObjectWithId> importProcess) {
                List<ObjectWithId> batch = new LinkedList<>();
                int cnt = 0;
                for (ObjectWithId o : list) {
                    batch.add(o);
                    if (++cnt == CHUNK_SIZE) {
                        processChunk(batch, importProcess);
                        cnt = 0;
                        batch = new LinkedList<>();
                    }
                }
                if (batch.size() > 0) {
                    processChunk(batch, importProcess);
                }
            }

            private void processChunk(final Collection<ObjectWithId> objects, final Process<ObjectWithId> action) {
                GuiComponentUtils.runLaterInEDT(() -> {
                    try {
                        for (ObjectWithId o : objects) {
                            action.apply(o);
                        }
                    } finally {
                        try {
                            barrier.await();
                        } catch (Exception e) {
                            log.error("Import action - await interrupted.", e);
                        }
                    }
                });
                try {
                    barrier.await();
                } catch (Exception e) {
                    log.error("Import - await interrupted.", e);
                }
            }

            @Override
            protected void eventDispatchActionAfter() {
                boolean cancelled = (Boolean) context.getAttribute("cancelled");
                importDialog.setSelectionSource(ExportImportSelectionSource.empty());
                if (cancelled) {
                    return;
                }
                List<ImportError> errors = new LinkedList<>();
                for (ImportComponent comp : imports.getImportComponents()) {
                    errors.addAll(imports.getErrors(comp));
                }

                // create string ...
                if (!errors.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    int lineLength = 70;
                    int nextLimit = lineLength;
                    for (ImportError error : errors) {
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
            // if train import -> move to appropriate group
            if (trainImport) {
                Group destGroup = groupDialog.getSelectedTo();
                ((Train) o).getAttributes().setRemove(Train.ATTR_GROUP, destGroup);
            }
        }
    }

    private String getText(ImportError error) {
        Object oid = error.getObject();
        String oidStr = null;
        if (oid instanceof Train) {
            oidStr = ((Train) oid).getName();
        } else if (oid instanceof Node) {
            oidStr = ((Node) oid).getName();
        } else if (oid instanceof TrainType) {
            oidStr = ((TrainType) oid).getDesc().translate();
        } else {
            oidStr = oid.toString();
        }
        return String.format("%s (%s)", oidStr, error.getText());
    }
}
