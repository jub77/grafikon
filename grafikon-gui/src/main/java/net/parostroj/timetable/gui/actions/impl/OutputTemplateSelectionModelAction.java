package net.parostroj.timetable.gui.actions.impl;

import java.util.stream.Collectors;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.CheckedModelAction;
import net.parostroj.timetable.gui.components.ExportImportSelection;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.imports.ImportMatch;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItem;
import net.parostroj.timetable.model.library.LibraryItemType;

public class OutputTemplateSelectionModelAction extends CheckedModelAction {

    public OutputTemplateSelectionModelAction(ActionContext context) {
        super(context);
    }

    @Override
    protected void action() {
        ExportImportSelection selection = new ExportImportSelection();
        selection.setImportOverwrite(true);
        selection.setImportMatch(ImportMatch.NAME);
        if (context.hasAttribute("library")) {
            Library library = (Library) context.getAttribute("library");
            selection.addItems(ImportComponent.OUTPUT_TEMPLATES,
                    library.getItems().get(LibraryItemType.OUTPUT_TEMPLATE).stream()
                            .map(LibraryItem::getObject).collect(Collectors.toList()));
        } else {
            TrainDiagram diagram = (TrainDiagram) context.getAttribute("diagram");
            selection.addItems(ImportComponent.OUTPUT_TEMPLATES, diagram.getOutputTemplates());
        }
        context.setAttribute("selection", selection);
    }
}
