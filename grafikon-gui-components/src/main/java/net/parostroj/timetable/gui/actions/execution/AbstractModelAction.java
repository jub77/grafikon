package net.parostroj.timetable.gui.actions.execution;

import net.parostroj.timetable.gui.actions.execution.ActionContext.WaitDialogState;

/**
 * Basic model action with context and helper method for wait dialog.
 *
 * @author jub
 */
public abstract class AbstractModelAction implements ModelAction {

    protected final ActionContext context;

    public AbstractModelAction(ActionContext context) {
        this.context = context;
    }

    @Override
    public ActionContext getActionContext() {
        return context;
    }

    protected void setWaitDialogVisible(boolean visible) {
        context.setState(visible ? WaitDialogState.SHOW : WaitDialogState.HIDE);
    }

    protected void setWaitMessage(String message) {
        context.setDescription(message);
    }

    protected void setProgressMessage(String message) {
        context.setProgressDescription(message);
    }

    protected void setWaitProgress(int progress) {
        context.setProgress(progress);
    }
}
