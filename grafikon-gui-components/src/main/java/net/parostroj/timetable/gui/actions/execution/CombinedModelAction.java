package net.parostroj.timetable.gui.actions.execution;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;

/**
 * Provides background action and methods running in EDT before and after it.
 *
 * @author jub
 */
public abstract class CombinedModelAction extends CheckedModelAction {

    public CombinedModelAction(ActionContext context) {
        super(context);
    }

    @Override
    final protected void action() {
        GuiComponentUtils.runNowInEDT(new Runnable() {

            @Override
            public void run() {
                eventDispatchActionBefore();
            }
        });
        this.backgroundAction();
        GuiComponentUtils.runNowInEDT(new Runnable() {

            @Override
            public void run() {
                eventDispatchActionAfter();
            }
        });
    }

    protected void eventDispatchActionBefore() {}

    protected void backgroundAction() {}

    protected void eventDispatchActionAfter() {}
}
