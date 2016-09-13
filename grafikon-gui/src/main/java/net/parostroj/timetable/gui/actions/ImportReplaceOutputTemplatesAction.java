package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class ImportReplaceOutputTemplatesAction extends AbstractAction {

    static final Logger log = LoggerFactory.getLogger(ImportReplaceOutputTemplatesAction.class);

    private ApplicationModel model;

    public ImportReplaceOutputTemplatesAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionHandler handler = ActionHandler.getInstance();
        ActionContext context = new ActionContext((Component) e.getSource());

        context.setAttribute("fileType", FileChooserFactory.Type.GTM_GTML);
        context.setAttribute("diagramImport", model.getDiagram());

        handler.execute(new OpenFileModelAction(context));
        handler.execute(new SelectLoadAction(context));
        handler.execute(new LoadDiagramModelAction(context));
        handler.execute(new LoadLibraryModelAction(context));
        handler.execute(new OutputTemplateSelectionModelAction(context));
        handler.execute(new ImportModelAction(context));
        handler.execute(new CopyTemplatesToOutputsModelAction(context, model));
    }
}
