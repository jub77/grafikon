package net.parostroj.timetable.gui.actions.impl;

import com.google.common.base.Predicate;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.EventDispatchModelAction;
import net.parostroj.timetable.gui.components.ExportImportSelectionSource;
import net.parostroj.timetable.gui.dialogs.ExportImportSelectionDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.library.Library;

/**
 * Import dialog action.
 *
 * <ul>
 * <li>diagram: TrainDiagram</li>
 * <li>library: Library</li>
 * <li>trainImport: ImportModelAction.TrainImportConfig</li>
 * <li>trainFilter: TrainGroupFilter</li>
 * <li>selection: ExportImportSelection</li>
 * </ul>
 *
 * @author jub
 */
public class ImportSelectionModelAction extends EventDispatchModelAction {

    public static final class TrainGroupFilter implements Predicate<ObjectWithId> {
        private final Group group;

        public TrainGroupFilter(Group group) {
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
                throw new IllegalArgumentException("Train expected: " + item);
            }
        }
    }

    public ImportSelectionModelAction(ActionContext context) {
        super(context);
    }

    @Override
    protected void eventDispatchAction() {
        TrainDiagram diagram = (TrainDiagram) context.getAttribute("diagram");
        Library library = (Library) context.getAttribute("library");
        boolean trainImport = context.hasAttribute("trainImport");
        TrainGroupFilter filter = (TrainGroupFilter) context.getAttribute("trainFilter");
        if (diagram != null || library != null) {

            final ExportImportSelectionDialog importDialog = new ExportImportSelectionDialog(
                    GuiComponentUtils.getWindow(context.getLocationComponent()), true);

            ExportImportSelectionSource source;
            if (trainImport) {
                source = ExportImportSelectionSource.fromDiagramSingleTypeWithFilter(diagram, ImportComponent.TRAINS,
                        filter::apply);
            } else {
                source = diagram != null ? ExportImportSelectionSource.fromDiagramToDiagram(diagram)
                        : ExportImportSelectionSource.fromLibraryToDiagram(library);
            }
            importDialog.setSelectionSource(source);
            importDialog.setLocationRelativeTo(context.getLocationComponent());
            importDialog.setVisible(true);
            context.setCancelled(importDialog.isCancelled());
            if (!context.isCancelled()) {
                context.setAttribute("selection", importDialog.getSelection());
            }
        }
    }
}
