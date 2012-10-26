package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.concurrent.CyclicBarrier;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import action.
 *
 * @author jub
 */
public class ImportAction extends AbstractAction {

    private final class TrainOidFilter implements Filter<ObjectWithId> {
        private final Group group;

        private TrainOidFilter(Group group) {
            this.group = group;
        }

        @Override
        public boolean is(ObjectWithId item) {
            if (item instanceof Train) {
                Group foundGroup = ((Train) item).getAttributes().get("group", Group.class);
                if (group == null)
                    return foundGroup == null;
                else
                    return group.equals(foundGroup);
            } else
                return true;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(ImportAction.class.getName());

    private final ImportDialog importDialog;
    private final GroupChooserFromToDialog groupDialog;
    private final ApplicationModel model;
    private final boolean trainImport;
    private final Collection<ImportComponent> components;

    public ImportAction(ApplicationModel model, Frame frame, boolean trainImport) {
        this.model = model;
        this.trainImport = trainImport;
        this.components = trainImport ? Collections.singleton(ImportComponent.TRAINS) : Arrays.asList(ImportComponent.values());
        this.importDialog = new ImportDialog(frame, true, trainImport ? Collections.singleton(ImportComponent.TRAINS) : null);
        this.groupDialog = trainImport ? new GroupChooserFromToDialog() : null;
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
            ModelAction loadAction = new LoadDiagramAction(context, selectedFile, parent, xmlFileChooser);
            handler.execute(loadAction);
        } else {
            // skip the rest
            return;
        }

        handler.execute(new EventDispatchModelAction(context) {

            @Override
            protected void eventDispatchAction() {
                TrainDiagram diagram = (TrainDiagram) context.getAttribute("diagram");
                boolean cancelled = false;
                Filter<ObjectWithId> filter = null;
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
                if (diagram != null && !cancelled) {
                    importDialog.setTrainDiagrams(model.getDiagram(), diagram, filter);
                    importDialog.setLocationRelativeTo(parent);
                    importDialog.setVisible(true);
                    cancelled = !importDialog.isSelected();
                }
                context.setAttribute("cancelled", cancelled);
            }
        });

        ModelAction importAction = new EventDispatchAfterModelAction(context) {

            private static final int CHUNK_SIZE = 10;
            private final Map<ImportComponent, Import> imports = new EnumMap<ImportComponent, Import>(ImportComponent.class);
            private boolean trainType;
            private int size;
            private final CyclicBarrier barrier = new CyclicBarrier(2);

            @Override
            protected void backgroundAction() {
                boolean cancelled = (Boolean) context.getAttribute("cancelled");
                if (cancelled)
                    return;
                setWaitMessage(ResourceLoader.getString("wait.message.import"));
                setWaitDialogVisible(true);
                long time = System.currentTimeMillis();
                try {
                    Map<ImportComponent, Set<ObjectWithId>> map = importDialog.getSelectedItems();
                    List<ObjectWithId> list = new LinkedList<ObjectWithId>();
                    for (ImportComponent comp : components) {
                        Set<ObjectWithId> set = map.get(comp);
                        list.addAll(set);
                        imports.put(comp, Import.getInstance(comp, importDialog.getDiagram(),
                                importDialog.getLibraryDiagram(), importDialog.getImportMatch()));
                    }
                    size = list.size();
                    if (size == 0)
                        return;
                    List<ObjectWithId> batch = new LinkedList<ObjectWithId>();
                    Iterator<ObjectWithId> iterator = list.iterator();
                    int cnt = 0;
                    while (iterator.hasNext()) {
                        ObjectWithId o = iterator.next();
                        batch.add(o);
                        if (++cnt == CHUNK_SIZE) {
                            processChunk(batch);
                            cnt = 0;
                            batch = new LinkedList<ObjectWithId>();
                        }
                    }
                    if (batch.size() > 0) {
                        processChunk(batch);
                    }
                } finally {
                    LOG.debug("Import finished in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            private void processChunk(final Collection<ObjectWithId> objects) {
                ModelActionUtilities.runLaterInEDT(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (ObjectWithId o : objects) {
                                Import i = imports.get(ImportComponent.getByComponentClass(o.getClass()));
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
                            try {
                                barrier.await();
                            } catch (Exception e) {
                                LOG.error("Import action - await interrupted.", e);
                            }
                        }
                    }
                });
                try {
                    barrier.await();
                } catch (Exception e) {
                    LOG.error("Import - await interrupted.", e);
                }
            }

            @Override
            protected void eventDispatchActionAfter() {
                boolean cancelled = (Boolean) context.getAttribute("cancelled");
                importDialog.clear();
                if (cancelled)
                    return;
                if (trainType) {
                    model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.TRAIN_TYPES_CHANGED, model));
                }
                List<Object> errors = new LinkedList<Object>();
                for (ImportComponent comp : components) {
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
            // if train import -> move to appropriate group
            if (trainImport) {
                Group destGroup = groupDialog.getSelectedTo();
                Train train = (Train) o;
                if (destGroup == null)
                    train.removeAttribute("group");
                else
                    train.setAttribute("group", destGroup);
            }
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
