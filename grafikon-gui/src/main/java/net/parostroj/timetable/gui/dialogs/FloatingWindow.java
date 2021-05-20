package net.parostroj.timetable.gui.dialogs;

import java.awt.Component;

import net.parostroj.timetable.gui.ini.StorableGuiData;

/**
 * Floating dialog window.
 *
 * @author jub
 */
public interface FloatingWindow extends StorableGuiData {

    String getTitle();

    void setVisibleOnInit();

    boolean isVisible();

    void setVisible(boolean visible);

    void setLocationRelativeTo(Component component);
}
