package net.parostroj.timetable.gui.actions.execution;

/**
 * Model action with a check if it should be run or not.
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
        return true;
    }
    
    protected abstract void action();
}
