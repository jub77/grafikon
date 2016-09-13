package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.CheckedModelAction;
import net.parostroj.timetable.gui.actions.execution.EventDispatchModelAction;
import net.parostroj.timetable.gui.actions.execution.ImportModelAction;
import net.parostroj.timetable.gui.actions.impl.FileChooserFactory;
import net.parostroj.timetable.gui.actions.impl.LoadDiagramModelAction;
import net.parostroj.timetable.gui.actions.impl.LoadLibraryModelAction;
import net.parostroj.timetable.gui.actions.impl.OpenFileModelAction;
import net.parostroj.timetable.gui.actions.impl.SelectLoadAction;
import net.parostroj.timetable.gui.components.ExportImportSelection;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.imports.ImportMatch;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItemType;

public class ImportReplaceOutputTemplatesAction extends AbstractAction {

    private static final Logger log = LoggerFactory.getLogger(ImportReplaceOutputTemplatesAction.class);

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
        handler.execute(new CheckedModelAction(context) {
            @Override
            protected void action() {
                ExportImportSelection selection = new ExportImportSelection();
                selection.setImportOverwrite(true);
                selection.setImportMatch(ImportMatch.NAME);
                if (context.hasAttribute("library")) {
                    Library library = (Library) context.getAttribute("library");
                    selection.addItems(ImportComponent.OUTPUT_TEMPLATES,
                            library.getItems().get(LibraryItemType.OUTPUT_TEMPLATE).stream()
                                    .map(item -> item.getObject()).collect(Collectors.toList()));
                } else {
                    TrainDiagram diagram = (TrainDiagram) context.getAttribute("diagram");
                    selection.addItems(ImportComponent.OUTPUT_TEMPLATES, diagram.getOutputTemplates());
                }
                context.setAttribute("selection", selection);
            }
        });
        handler.execute(new ImportModelAction(context));
        handler.execute(new EventDispatchModelAction(context) {
            @Override
            protected void eventDispatchAction() {
                ScriptAction scriptAction = model.getScriptsLoader().getScriptAction("copy_output_templates_to_outputs");
                try {
                    scriptAction.execute(model.getDiagram());
                } catch (GrafikonException e) {
                    log.error(e.getMessage(), e);
                    GuiComponentUtils.showError("Cannot create outputs: " + e.getMessage(), context.getLocationComponent());
                }
            }
        });
    }
}
