package net.parostroj.timetable.gui.actions.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.scripts.ScriptAction;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.EventDispatchModelAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.GrafikonException;

public class CopyTemplatesToOutputsModelAction extends EventDispatchModelAction {

    private static final Logger log = LoggerFactory.getLogger(CopyTemplatesToOutputsModelAction.class);

    private ApplicationModel model;

    public CopyTemplatesToOutputsModelAction(ActionContext context, ApplicationModel model) {
        super(context);
        this.model = model;
    }

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
}