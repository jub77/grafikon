package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ImportModelAction;
import net.parostroj.timetable.gui.actions.impl.CopyTemplatesToOutputsModelAction;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.LoadDiagramModelAction;
import net.parostroj.timetable.gui.actions.impl.LoadLibraryModelAction;
import net.parostroj.timetable.gui.actions.impl.OpenFileModelAction;
import net.parostroj.timetable.gui.actions.impl.OutputTemplateSelectionModelAction;
import net.parostroj.timetable.gui.actions.impl.SelectLoadAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.TrainDiagramType;

public class ImportReplaceOutputTemplatesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private final transient ApplicationModel model;

    public ImportReplaceOutputTemplatesAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionHandler handler = ActionHandler.getInstance();
        ActionContext context = new ActionContext(GuiComponentUtils.getTopLevelComponent(e.getSource()));

        context.setAttribute("fileType", FileChooserFactory.Type.GTM_GTML);
        context.setAttribute("diagramImport", model.getDiagram());
        TrainDiagramType diagramType = model.getProgramSettings().getDiagramType();

        handler.execute(new OpenFileModelAction(context));
        handler.execute(new SelectLoadAction(context));
        handler.execute(new LoadDiagramModelAction(context, diagramType));
        handler.execute(new LoadLibraryModelAction(context, diagramType));
        handler.execute(new OutputTemplateSelectionModelAction(context));
        handler.execute(new ImportModelAction(context));
        handler.execute(new CopyTemplatesToOutputsModelAction(context, model));
    }
}
