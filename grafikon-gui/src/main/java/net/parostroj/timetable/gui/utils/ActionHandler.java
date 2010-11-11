package net.parostroj.timetable.gui.utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invokes action and show dialog after specified amount of time
 * 
 * @author jub
 */
public class ActionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ActionHandler.class.getName());

    private static ActionHandler instance = new ActionHandler();

    private static int DEFAULT_WAIT_TIME = 200;

    public static ActionHandler getInstance() {
        return instance;
    }
    
    /**
     * Private constructor to prevent outside instantiation.
     */
    private ActionHandler() {}
    
    public void executeAction(Component component, String comment, ModelAction action) {
        this.executeAction(component, comment, DEFAULT_WAIT_TIME, action);
    }
    
    public void executeAction(Component component, String comment, int waitTime, ModelAction action) {
        SwingWorker<Void, Void> worker = this.createWorker(action);
        this.executeAction(component, comment, waitTime, worker);
    }

    public void executeAction(Component component, String comment, SwingWorker<?, ?> worker) {
        this.executeAction(component, comment, DEFAULT_WAIT_TIME, worker);
    }

    public void executeAction(Component component, String comment, int waitTime, SwingWorker<?, ?> worker) {
        Timer timer = this.createTimer(worker, waitTime, component, comment);
        timer.start();
        worker.execute();
    }
    
    private Timer createTimer(final SwingWorker<?, ?> worker,int waitTime, final Component component, final String message) {
        Timer timer = new Timer(waitTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (worker.getState() != SwingWorker.StateValue.DONE) {
                    LOG.trace("Waiting dialog initialization.");
                    WaitDialog dialog = createWaitDialog(component, message);
                    worker.addPropertyChangeListener(dialog);
                    dialog.setVisible(true);
                } else {
                    LOG.trace("Waiting dialog initialization skipped - action already finished.");
                }
            }
        });
        timer.setInitialDelay(waitTime);
        timer.setRepeats(false);
        return timer;
    }
    
    private SwingWorker<Void, Void> createWorker(final ModelAction action) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                // run action ...
                action.run();
                return null;
            }

            @Override
            protected void done() {
                // run after action code in event dispatch thread
                action.afterRun();
            }
        };
        return worker;
    }
    
    private WaitDialog createWaitDialog(Component component, String message) {
        Window top = (component != null) ? SwingUtilities.getWindowAncestor(component) : null;
        LOG.trace("Component: " + ((component == null) ? "<null>" : component.getClass().getName()));
        LOG.trace("Top: " + ((top == null) ? "<null>" : top.getClass().getName()));
        WaitDialog waitDialog = (component instanceof Dialog) ?
                new WaitDialog((Dialog)component, true) :
                new WaitDialog((Frame)component, true);
        if (top != null) {
            waitDialog.setLocationRelativeTo(top);
        } else {
            waitDialog.setLocationRelativeTo(component);
        }
        waitDialog.setMessage(message);
        return waitDialog;
    }
}
