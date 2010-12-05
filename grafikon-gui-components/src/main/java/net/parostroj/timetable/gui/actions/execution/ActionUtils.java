package net.parostroj.timetable.gui.actions.execution;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Utility class.
 *
 * @author jub
 */
public class ActionUtils {
    
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
}
