package net.parostroj.timetable.gui.modelactions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import net.parostroj.timetable.gui.utils.WaitDialog;

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
    
    private Executor executor;

    public static ActionHandler getInstance() {
        return instance;
    }
    
    /**
     * Private constructor to prevent outside instantiation.
     */
    private ActionHandler() {
        executor = Executors.newSingleThreadExecutor();
    }
    
    public void executeActionWithoutDialog(Runnable runnable) {
        executor.execute(runnable);
    }
    
    public void executeAction(Component component, String comment, ModelAction action) {
        this.executeAction(component, comment, DEFAULT_WAIT_TIME, action);
    }
    
    public void executeAction(Component component, String comment, int waitTime, ModelAction modelAction) {
        SwingWorker<Void, Void> worker = this.createWorker(modelAction);
        this.executeAction(component, comment, waitTime, worker);
    }

    public void executeAction(Component component, String comment, SwingWorker<?, ?> worker) {
        this.executeAction(component, comment, DEFAULT_WAIT_TIME, worker);
    }

    public void executeAction(Component component, String comment, int waitTime, SwingWorker<?, ?> worker) {
        final Timer timer = this.createTimer(worker, waitTime, component, comment);
        worker.addPropertyChangeListener(new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                LOG.trace(String.format("Event received: %s, %s, %s", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                if ("state".equals(evt.getPropertyName())) {
                    if (evt.getNewValue() == SwingWorker.StateValue.STARTED) {
                        timer.start();
                    }
                }
            }
        });
        executor.execute(worker);
    }
    
    private Timer createTimer(final SwingWorker<?, ?> worker, int waitTime, final Component component, final String message) {
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

            private long time;

            @Override
            protected Void doInBackground() throws Exception {
                time = System.currentTimeMillis();
                // run action ...
                action.run();
                return null;
            }

            @Override
            protected void done() {
                time = System.currentTimeMillis() - time;
                LOG.debug("{} finished in {}ms", action.getActionName(), time);
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
