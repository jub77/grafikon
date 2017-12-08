package net.parostroj.timetable.gui.dialogs;

import java.awt.Component;

import net.parostroj.timetable.gui.ini.StorableGuiData;

/**
 * Floating dialog window.
 *
 * @author jub
 */
public interface FloatingWindow extends StorableGuiData {

    public String getTitle();

    public void setVisibleOnInit();

    public boolean isVisible();

    public void setVisible(boolean visible);

    public void setLocationRelativeTo(Component component);
}
