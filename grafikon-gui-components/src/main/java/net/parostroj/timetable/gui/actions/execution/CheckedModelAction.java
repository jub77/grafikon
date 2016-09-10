package net.parostroj.timetable.gui.actions.execution;

/**
 * Model action with a check if it should be run or not - default implementation checks cancelled attribute
 * of contect.
 *
 * @author jub
 */
public abstract class CheckedModelAction extends AbstractModelAction {

    public static final String CANCELLED_ATTRIBUTE = "cancelled";

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
        Boolean cancelled = context.getAttribute(CANCELLED_ATTRIBUTE, Boolean.class);
        return !Boolean.TRUE.equals(cancelled);
    }

    protected abstract void action();
}
