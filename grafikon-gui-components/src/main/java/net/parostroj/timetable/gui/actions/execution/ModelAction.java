package net.parostroj.timetable.gui.actions.execution;

/**
 * Common interface for all background actions.
 *
 * @author jub
 */
public interface ModelAction extends Runnable {

    ActionContext getActionContext();

    static ModelAction newAction(final ActionContext context, final Runnable action) {
        return new CheckedModelAction(context) {
            @Override
            protected void action() {
                action.run();
            }
        };
    }

    static ModelAction newEdtAction(final ActionContext context, final Runnable action) {
        return new EventDispatchModelAction(context) {
            @Override
            protected void eventDispatchAction() {
                action.run();
            }
        };
    }
}
