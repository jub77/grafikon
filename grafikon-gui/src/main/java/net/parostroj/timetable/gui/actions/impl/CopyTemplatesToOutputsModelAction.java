package net.parostroj.timetable.gui.actions.impl;

import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.templates.OutputTemplateStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.EventDispatchModelAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.GrafikonException;

import java.util.Collection;
import java.util.Map;

public class CopyTemplatesToOutputsModelAction extends EventDispatchModelAction {

    private static final Logger log = LoggerFactory.getLogger(CopyTemplatesToOutputsModelAction.class);

    private final ApplicationModel model;
    private final OutputTemplateStorage.Category category;
    private final Collection<OutputTemplate> templates;

    public CopyTemplatesToOutputsModelAction(ActionContext context, ApplicationModel model,
            OutputTemplateStorage.Category category, Collection<OutputTemplate> templates) {
        super(context);
        this.model = model;
        this.category = category;
        this.templates = templates;
    }

    @Override
    protected void eventDispatchAction() {
        ScriptAction scriptAction = model.getScriptsLoader().getScriptActionsMap().get("copy_output_templates_to_outputs");
        try {
            Map<String, Object> params = Map.of();
            if (category != null) {
                params = Map.of("category", category);
            } else if (templates != null) {
                params = Map.of("templates", templates);
            }
            scriptAction.execute(model.getDiagram(), params);
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
            GuiComponentUtils.showError("Cannot create outputs: " + e.getMessage(), context.getLocationComponent());
        }
    }
}
