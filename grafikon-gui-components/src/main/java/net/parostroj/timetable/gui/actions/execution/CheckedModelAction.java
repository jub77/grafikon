package net.parostroj.timetable.gui.actions.execution;

/**
 * Model action with a check if it should be run or not - default implementation checks cancelled attribute
 * of contect.
 *
 * @author jub
 */
public abstract class CheckedModelAction extends AbstractModelAction {

    public CheckedModelAction(ActionContext context) {
        super(context);
    }

    @Override
    final public void run() {
        // check fails -> return
        if (!this.check())
            return;
        this.action();
    }

    protected boolean check() {
        return !getActionContext().isCancelled();
    }

    protected abstract void action();
}
