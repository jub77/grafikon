package net.parostroj.timetable.gui.actions;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.impl.CopyTemplatesToOutputsModelAction;
import net.parostroj.timetable.gui.actions.impl.LoadLibraryUrlModelAction;
import net.parostroj.timetable.gui.actions.impl.OutputTemplateSelectionModelAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.TrainDiagramType;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ImportReplaceOutputTemplatesUrlAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "output_templates.gtml";

    private final transient ApplicationModel model;

    public ImportReplaceOutputTemplatesUrlAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionHandler handler = ActionHandler.getInstance();
        ActionContext context = new ActionContext(GuiComponentUtils.getTopLevelComponent(e.getSource()));

        context.setAttribute("diagramImport", model.getDiagram());

        String url = model.getLibraryBaseUrl() + "/" + TEMPLATE;
        context.setAttribute("libraryUrl", url);

        handler.execute(new LoadLibraryUrlModelAction(context, TrainDiagramType.RAW));
        handler.execute(new OutputTemplateSelectionModelAction(context));
        handler.execute(new CopyTemplatesToOutputsModelAction(context, model));
    }
}
