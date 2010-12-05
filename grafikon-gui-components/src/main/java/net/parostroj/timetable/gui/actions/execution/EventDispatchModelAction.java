package net.parostroj.timetable.gui.actions.execution;

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
    final protected void action() {
        ModelActionUtilities.runNowInEDT(new Runnable() {
            
            @Override
            public void run() {
                eventDispatchAction();
            }
        });
    }

    protected abstract void eventDispatchAction();
}
