package net.parostroj.timetable.gui.dialogs;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.gui.StorableGuiData;

/**
 * List of floating dialogs.
 *
 * @author jub
 */
public class FloatingDialogsList extends ArrayList<FloatingDialog> implements StorableGuiData {

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        for (FloatingDialog dialog : this)
            dialog.saveToPreferences(prefs);
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        for (FloatingDialog dialog : this)
            dialog.loadFromPreferences(prefs);
    }

    public void addToMenuItem(final JMenuItem menuItem) {
        for (final FloatingDialog dialog : this) {
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
        for (FloatingDialog dialog : this)
            dialog.setVisibleOnInit();
    }
}
