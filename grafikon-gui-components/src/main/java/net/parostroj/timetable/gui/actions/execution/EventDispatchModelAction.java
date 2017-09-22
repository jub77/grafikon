package net.parostroj.timetable.gui.actions.execution;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;

/**
 * Model action with a method which is executed in event dispatch thread.
 *
 * @author jub
 */
public abstract class EventDispatchModelAction extends CheckedModelAction {

    public EventDispatchModelAction(ActionContext context) {
        super(context);
    }

    @Override
    protected final void action() {
        GuiComponentUtils.runNowInEDT(this::eventDispatchAction);
    }

    protected abstract void eventDispatchAction();
}
