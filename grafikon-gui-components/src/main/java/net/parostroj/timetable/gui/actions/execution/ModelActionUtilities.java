package net.parostroj.timetable.gui.actions.execution;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper methods for model actions.
 * 
 * @author jub
 */
public class ModelActionUtilities {
    
    private static final Logger LOG = LoggerFactory.getLogger(ModelActionUtilities.class);
    
    public static void runInEDT(Runnable runnable, boolean now) {
        if (now)
            runNowInEDT(runnable);
        else
            runLaterInEDT(runnable);
    }

    public static void runNowInEDT(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread())
            runnable.run();
        else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception e) {
                LOG.error("Error invoking runnable.", e);
            }
        }
    }
    
    public static void runLaterInEDT(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
}
