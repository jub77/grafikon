
package net.parostroj.timetable.gui.actions;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.impl.CopyTemplatesToOutputsModelAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.templates.OutputTemplateStorage;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CreateOutputsResourcesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private final transient ApplicationModel model;
    private final OutputTemplateStorage.Category category;

    public CreateOutputsResourcesAction(ApplicationModel model, OutputTemplateStorage.Category category) {
        this.model = model;
        this.category = category;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionHandler handler = ActionHandler.getInstance();
        ActionContext context = new ActionContext(GuiComponentUtils.getTopLevelComponent(e.getSource()));

        handler.execute(new CopyTemplatesToOutputsModelAction(context, model, category));
    }
}
