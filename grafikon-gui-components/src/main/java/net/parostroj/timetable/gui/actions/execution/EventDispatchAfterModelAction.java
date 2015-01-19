package net.parostroj.timetable.gui.actions.execution;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;

/**
 * Model action with a method which is executed in event dispatch thread after
 * background action.
 *
 * @author jub
 */
public abstract class EventDispatchAfterModelAction extends CheckedModelAction {

    public EventDispatchAfterModelAction(ActionContext context) {
        super(context);
    }

    @Override
    final protected void action() {
        this.backgroundAction();
        GuiComponentUtils.runNowInEDT(new Runnable() {

            @Override
            public void run() {
                eventDispatchActionAfter();
            }
        });
    }

    protected void backgroundAction() {}

    protected void eventDispatchActionAfter() {}
}
