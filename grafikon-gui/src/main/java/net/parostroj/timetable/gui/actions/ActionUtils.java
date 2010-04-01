package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * Utility class.
 *
 * @author jub
 */
public class ActionUtils {

    public static Component getTopLevelComponent(Object component) {
        if (component == null || !(component instanceof JComponent)) {
            return null;
        }
        JComponent jComponent = (JComponent) component;
        // start with top level ancestor
        Component top = jComponent.getTopLevelAncestor();
        if (top == null && jComponent.getParent() != null) {
            // try JMenuItem
            Component c = jComponent;
            while (c.getParent() != null) {
                c = c.getParent();
            }
            if (c instanceof JPopupMenu) {
                c = ((JPopupMenu) c).getInvoker();
                if (c instanceof JComponent) {
                    top = ((JComponent) c).getTopLevelAncestor();
                }
            }
        }
        return top;
    }
}
