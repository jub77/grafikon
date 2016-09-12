package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.actions.impl.CloseableFileChooser;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.LoadDiagramModelAction;
import net.parostroj.timetable.gui.actions.impl.LoadLibraryModelAction;
import net.parostroj.timetable.gui.components.ExportImportSelectionSource;
import net.parostroj.timetable.gui.dialogs.ExportImportSelectionDialog;
import net.parostroj.timetable.gui.dialogs.GroupChooserFromToDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.library.Library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

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

    static final Logger log = LoggerFactory.getLogger(ImportAction.class);

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
                context.setAttribute("file", selectedFile);
                ModelAction loadAction = selectedFile.getName().endsWith(".gtm") ?
                        new LoadDiagramModelAction(context) :
                            new LoadLibraryModelAction(context);
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
                context.setAttribute("selection", importDialog.getSelection());
                if (trainImport) {
                    context.setAttribute("trainImport", new ImportModelAction.TrainImportConfig(groupDialog.isRemoveExistingTrains(),
                            groupDialog.getSelectedFrom(), groupDialog.getSelectedTo()));
                }
                context.setAttribute("diagram", model.getDiagram());
            }
        });

        handler.execute(new ImportModelAction(context));
    }
}
