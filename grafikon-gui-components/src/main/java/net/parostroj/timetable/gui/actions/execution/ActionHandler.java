package net.parostroj.timetable.gui.actions.execution;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.parostroj.timetable.gui.dialogs.WaitDialog;

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

    private final ExecutorService executor;
    private final WaitDialog waitDialog;

    private ActionHandler() {
        executor = Executors.newSingleThreadExecutor();
        waitDialog = new WaitDialog(true);
    }

    public void setWaitIconImage(Image waitIconImage) {
        this.waitDialog.setIconImage(waitIconImage);
    }

    public void execute(ModelAction action) {
        action.getActionContext().addPropertyChangeListener(waitDialog);
        executor.execute(action);
    }
}
