package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class.
 *
 * @author jub
 */
public class ActionUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(ActionUtils.class);

    public static Component getTopLevelComponent(Object component) {
        if (component == null || !(component instanceof Component)) {
            return null;
        } else {
            return getWindow((Component)component);
        }
    }

    public static Window getWindow(Component comp) {
        while (comp != null && !(comp instanceof Window)) {
            if (comp instanceof JPopupMenu) {
                comp = ((JPopupMenu) comp).getInvoker();
            } else {
                comp = comp.getParent();
            }
        }
        return (Window) comp;
    }

    public static void showError(String text, Component parent) {
        JOptionPane.showMessageDialog(parent, text, ResourceLoader.getString("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(String text, Component parent) {
        JOptionPane.showMessageDialog(parent, text, ResourceLoader.getString("dialog.warning.title"), JOptionPane.WARNING_MESSAGE);
    }
    
    public static void runInEDT(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread())
            runnable.run();
        else
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception e) {
                LOG.error("Error execution action in dispatch thread.", e);
            }
    }
}
