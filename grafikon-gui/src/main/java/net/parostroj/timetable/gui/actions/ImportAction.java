package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import java.util.function.Predicate;
import javax.swing.AbstractAction;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.EventDispatchModelAction;
import net.parostroj.timetable.gui.actions.execution.ImportModelAction;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.ImportSelectionModelAction;
import net.parostroj.timetable.gui.actions.impl.ImportSelectionModelAction.TrainGroupFilter;
import net.parostroj.timetable.gui.actions.impl.LoadDiagramModelAction;
import net.parostroj.timetable.gui.actions.impl.LoadLibraryModelAction;
import net.parostroj.timetable.gui.actions.impl.OpenFileModelAction;
import net.parostroj.timetable.gui.actions.impl.SelectLoadAction;
import net.parostroj.timetable.gui.dialogs.GroupChooserFromToDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Import action.
 *
 * @author jub
 */
public class ImportAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private final GroupChooserFromToDialog groupDialog;
    private final transient ApplicationModel model;
    private final boolean trainImport;
    private final boolean supportLibrary;

    public ImportAction(ApplicationModel model, boolean trainImport, boolean supportLibrary) {
        this.model = model;
        this.trainImport = trainImport;
        this.supportLibrary = supportLibrary;
        this.groupDialog = trainImport ? new GroupChooserFromToDialog() : null;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Component parent = GuiComponentUtils.getTopLevelComponent(event.getSource());

        ActionContext context = new ActionContext(parent);
        ActionHandler handler = ActionHandler.getInstance();

        context.setAttribute("fileType", supportLibrary ? FileChooserFactory.Type.GTM_GTML : FileChooserFactory.Type.GTM);
        context.setAttribute("diagramImport", model.getDiagram());

        handler.execute(new OpenFileModelAction(context));
        handler.execute(new SelectLoadAction(context));
        handler.execute(new LoadDiagramModelAction(context));
        handler.execute(new LoadLibraryModelAction(context));
        handler.execute(new EventDispatchModelAction(context) {

            @Override
            protected void eventDispatchAction() {
                TrainDiagram diagram = (TrainDiagram) context.getAttribute("diagram");
                boolean cancelled = false;
                Predicate<ObjectWithId> filter = null;
                if (trainImport) {
                    groupDialog.setLocationRelativeTo(parent);
                    groupDialog.showDialog(diagram, null, model.getDiagram(), null);
                    if (groupDialog.isSelected()) {
                        final Group group = groupDialog.getSelectedFrom();
                        filter = new TrainGroupFilter(group);
                    } else {
                        cancelled = !groupDialog.isSelected();
                    }
                }
                context.setAttribute("trainFilter", filter);
                context.setCancelled(cancelled);
                if (trainImport) {
                    context.setAttribute("trainImport", new ImportModelAction.TrainImportConfig(
                            groupDialog.isRemoveExistingTrains(),
                            groupDialog.getSelectedTo()));
                }
            }
        });

        handler.execute(new ImportSelectionModelAction(context));
        handler.execute(new ImportModelAction(context));
    }
}
