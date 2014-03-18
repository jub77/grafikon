package net.parostroj.timetable.gui.utils;

import java.awt.Insets;

import javax.swing.*;

/**
 * GUI utility.
 *
 * @author jub
 */
public class GuiComponentUtils {

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
}
