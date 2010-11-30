package net.parostroj.timetable.gui.actions.execution;

import java.awt.Frame;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.parostroj.timetable.gui.utils.WaitDialog;

/**
 * Action handler - executes model actions.
 *
 * @author jub
 */
public class ActionHandler {

    private static final ActionHandler instance = new ActionHandler();

    public static ActionHandler getInstance() {
        return instance;
    }

    public static ActionHandler createInstance() {
        return new ActionHandler();
    }

    private ExecutorService executor;
    private WaitDialog waitDialog;

    private ActionHandler() {
        executor = Executors.newSingleThreadExecutor();
        waitDialog = new WaitDialog((Frame) null, true);
    }

    public void execute(ModelAction action) {
        action.getActionContext().addPropertyChangeListener(waitDialog);
        executor.execute(action);
    }
}
