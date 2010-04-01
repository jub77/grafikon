package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Window;
import javax.swing.JPopupMenu;

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
}
