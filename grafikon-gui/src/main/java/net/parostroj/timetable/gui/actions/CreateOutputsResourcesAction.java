
package net.parostroj.timetable.gui.actions;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.impl.CopyTemplatesToOutputsModelAction;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CreateOutputsResourcesAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private final transient ApplicationModel model;

    public CreateOutputsResourcesAction(ApplicationModel model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionHandler handler = ActionHandler.getInstance();
        ActionContext context = new ActionContext(GuiComponentUtils.getTopLevelComponent(e.getSource()));

        handler.execute(new CopyTemplatesToOutputsModelAction(context, model));
    }
}
