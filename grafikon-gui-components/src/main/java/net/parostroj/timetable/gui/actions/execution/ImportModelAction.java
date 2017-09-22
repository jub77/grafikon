package net.parostroj.timetable.gui.actions.execution;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.filters.ModelPredicates;
import net.parostroj.timetable.gui.components.ExportImportSelection;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.imports.TrainDiagramPartImport;
import net.parostroj.timetable.model.imports.Import.ImportError;

/**
 * Import of selection.
 * <br>
 * Context attributes:
 * <ul>
 * <li>diagramImport: TrainDiagram</li>
 * <li>trainImport: TrainImportConfig</li>
 * <li>selection: ExportImportSelection</li>
 * </ul>
 *
 * @author jub
 */
public class ImportModelAction extends EventDispatchAfterModelAction {

    private static final Logger log = LoggerFactory.getLogger(ImportModelAction.class);

    public static class TrainImportConfig {
        private boolean removeExisting;
        private Group fromGroup;
        private Group toGroup;

        public TrainImportConfig(boolean removeExisting, Group fromGroup, Group toGroup) {
            this.removeExisting = removeExisting;
            this.fromGroup = fromGroup;
            this.toGroup = toGroup;
        }

        public boolean isRemoveExisting() {
            return removeExisting;
        }

        public Group getFromGroup() {
            return fromGroup;
        }

        public Group getToGroup() {
            return toGroup;
        }
    }

    private static final int CHUNK_SIZE = 10;
    private TrainDiagramPartImport imports;
    private final CyclicBarrier barrier = new CyclicBarrier(2);

    public ImportModelAction(ActionContext context) {
        super(context);
    }

    @Override
    protected void backgroundAction() {
        setWaitMessage(ResourceLoader.getString("wait.message.import"));
        setWaitDialogVisible(true);
        long time = System.currentTimeMillis();
        try {
            ExportImportSelection selection = context.getAttribute("selection", ExportImportSelection.class);
            TrainDiagram diagram = context.getAttribute("diagramImport", TrainDiagram.class);
            TrainImportConfig trainImportConfig = context.getAttribute("trainImport", TrainImportConfig.class);
            Map<ImportComponent, Collection<ObjectWithId>> map = selection.getObjectMap();
            imports = new TrainDiagramPartImport(diagram, selection.getImportMatch(), selection.isImportOverwrite());
            List<ObjectWithId> list = map.values().stream().sequential().flatMap(item -> item.stream().sequential()).collect(Collectors.toList());
            if (list.isEmpty()) {
                return;
            }
            if (trainImportConfig != null && trainImportConfig.isRemoveExisting()) {
                // remove existing trains in group
                Consumer<ObjectWithId> deleteProcess = item -> diagram.getTrains().remove(item);
                Iterable<Train> filteredTrains = Iterables.filter(diagram.getTrains(), ModelPredicates.inGroup(trainImportConfig.getToGroup()));
                processItems(filteredTrains, deleteProcess);
            }
            // import new objects
            Consumer<ObjectWithId> importProcess = item -> {
                ImportComponent i = ImportComponent.getByComponentClass(item.getClass());
                if (i != null) {
                    ObjectWithId imported = imports.importPart(item);
                    processImportedObject(imported, trainImportConfig);
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

    private void processItems(Iterable<? extends ObjectWithId> list, Consumer<ObjectWithId> importProcess) {
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
        if (!batch.isEmpty()) {
            processChunk(batch, importProcess);
        }
    }

    private void processChunk(final Collection<ObjectWithId> objects, final Consumer<ObjectWithId> action) {
        GuiComponentUtils.runLaterInEDT(() -> {
            try {
                for (ObjectWithId o : objects) {
                    action.accept(o);
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
        if (context.isCancelled()) {
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
            JOptionPane.showConfirmDialog(getActionContext().getLocationComponent(), message,
                    ResourceLoader.getString("import.warning.title"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        }
    }

    private String getText(ImportError error) {
        Object oid = error.getObject();
        String oidStr = null;
        if (oid instanceof Train) {
            oidStr = ((Train) oid).getDefaultName();
        } else if (oid instanceof Node) {
            oidStr = ((Node) oid).getName();
        } else if (oid instanceof TrainType) {
            oidStr = ((TrainType) oid).getDesc().translate();
        } else {
            oidStr = oid.toString();
        }
        return String.format("%s (%s)", oidStr, error.getText());
    }

    private void processImportedObject(ObjectWithId o, TrainImportConfig trainImportConfig) {
        // process new trains
        // if train import -> move to appropriate group
        if (o instanceof Train && trainImportConfig != null) {
            Group destGroup = trainImportConfig.getToGroup();
            ((Train) o).getAttributes().setRemove(Train.ATTR_GROUP, destGroup);
        }
    }
}
