package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;

/**
 * List of floating dialogs.
 *
 * @author jub
 */
public class FloatingWindowsList extends ArrayList<FloatingWindow> implements StorableGuiData {

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        for (FloatingWindow dialog : this) {
            dialog.saveToPreferences(prefs);
        }
        return null;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        for (FloatingWindow dialog : this) {
            dialog.loadFromPreferences(prefs);
        }
        return null;
    }

    public void addToMenuItem(final JMenuItem menuItem) {
        for (final FloatingWindow dialog : this) {
            // use title for menu item text
            JMenuItem fdItem = new JMenuItem();
            fdItem.setAction(new AbstractAction(dialog.getTitle()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!dialog.isVisible())
                        dialog.setVisible(true);
                    else
                        dialog.setLocationRelativeTo(menuItem.getTopLevelAncestor());
                }
            });
            menuItem.add(fdItem);
        }
    }

    public void setVisibleOnInit() {
        for (FloatingWindow dialog : this)
            dialog.setVisibleOnInit();
    }
}
