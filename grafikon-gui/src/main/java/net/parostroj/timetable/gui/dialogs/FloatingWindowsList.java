package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.ini4j.Ini;

import net.parostroj.timetable.gui.StorableGuiData;

/**
 * List of floating dialogs.
 *
 * @author jub
 */
public class FloatingWindowsList extends ArrayList<FloatingWindow> implements StorableGuiData {

    @Override
    public Ini.Section saveToPreferences(Ini prefs) {
        for (FloatingWindow dialog : this) {
            dialog.saveToPreferences(prefs);
        }
        return null;
    }

    @Override
    public Ini.Section loadFromPreferences(Ini prefs) {
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
