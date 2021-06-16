package net.parostroj.timetable.gui.actions.execution;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;

/**
 * Model action with a method which is executed in event dispatch thread after
 * background action.
 *
 * @author jub
 */
public abstract class EventDispatchAfterModelAction extends CheckedModelAction {

    protected EventDispatchAfterModelAction(ActionContext context) {
        super(context);
    }

    @Override
    protected final void action() {
        this.backgroundAction();
        GuiComponentUtils.runNowInEDT(this::eventDispatchActionAfter);
    }

    protected void backgroundAction() {}

    protected void eventDispatchActionAfter() {}
}
