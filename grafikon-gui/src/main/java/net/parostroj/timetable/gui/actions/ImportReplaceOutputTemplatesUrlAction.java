package net.parostroj.timetable.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ImportModelAction;
import net.parostroj.timetable.gui.actions.impl.CopyTemplatesToOutputsModelAction;
import net.parostroj.timetable.gui.actions.impl.LoadLibraryUrlModelAction;
import net.parostroj.timetable.gui.actions.impl.OutputTemplateSelectionModelAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;

public class ImportReplaceOutputTemplatesUrlAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

	static final Logger log = LoggerFactory.getLogger(ImportReplaceOutputTemplatesUrlAction.class);

    private static final String TEMPLATE = "output_templates.gtml";

    private ApplicationModel model;

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
        log.debug("Loading library: {}", url);

        handler.execute(new LoadLibraryUrlModelAction(context));
        handler.execute(new OutputTemplateSelectionModelAction(context));
        handler.execute(new ImportModelAction(context));
        handler.execute(new CopyTemplatesToOutputsModelAction(context, model));
    }
}
