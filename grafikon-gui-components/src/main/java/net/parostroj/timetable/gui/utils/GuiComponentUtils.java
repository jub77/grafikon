package net.parostroj.timetable.gui.utils;

import java.awt.Component;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.*;

import org.beanfabrics.swing.BnButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI utility.
 *
 * @author jub
 */
public class GuiComponentUtils {

    private static final Logger log = LoggerFactory.getLogger(GuiComponentUtils.class);

    public static BnButton createBnButton(GuiIcon icon, int margin) {
        return createButton(new BnButton(), icon, margin);
    }

    /**
     * creates button with margin and icon.
     *
     * @param icon icon
     * @param margin margin
     * @return button
     */
    public static JButton createButton(GuiIcon icon, int margin) {
        return createButton(new JButton(), icon, margin);
    }

    /**
     * creates button with margin and icon.
     *
     * @param icon icon
     * @param margin margin
     * @param action action
     * @return button
     */
    public static JButton createButton(GuiIcon icon, int margin, Action action) {
        return createButton(new JButton(action), icon, margin);
    }

    /**
     * creates toggle button with margin and icon.
     *
     * @param icon icon
     * @param margin margin
     * @return button
     */
    public static JToggleButton createToggleButton(GuiIcon icon, int margin) {
        return createButton(new JToggleButton(), icon, margin);
    }

    public static <T extends AbstractButton> T createButton(T button, GuiIcon icon, int margin) {
        button.setIcon(ResourceLoader.createImageIcon(icon));
        button.setMargin(new Insets(margin, margin, margin, margin));
        return button;
    }

    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

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

    public static void showInformation(String text, Component parent) {
        JOptionPane.showMessageDialog(parent, text, ResourceLoader.getString("dialog.info.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(String text, Component parent) {
        JOptionPane.showMessageDialog(parent, text, ResourceLoader.getString("dialog.warning.title"), JOptionPane.WARNING_MESSAGE);
    }

    public static void runInEDT(Runnable runnable, boolean now) {
        if (now) {
            runNowInEDT(runnable);
        } else {
            runLaterInEDT(runnable);
        }
    }

    public static void runLaterInEDT(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public static void runNowInEDT(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception e) {
                log.error("Error invoking runnable.", e);
            }
        }
    }
}
