package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;

import javax.swing.JComponent;

import org.ini4j.Ini;

import net.parostroj.timetable.gui.ini.AppPreferences;
import net.parostroj.timetable.gui.utils.GuiUtils;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Floating dialog window.
 *
 * @author jub
 */
public class FloatingDialog extends javax.swing.JDialog implements FloatingWindow {

    private final String storageKeyPrefix;
    private boolean visibleOnInit;

    public FloatingDialog(Frame parent, JComponent panel, String titleKey, String storageKeyPrefix) {
        super(parent, false);
        if (titleKey != null)
            this.setTitle(ResourceLoader.getString(titleKey));
        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        this.storageKeyPrefix = storageKeyPrefix;

        // update layout
        pack();
    }

    @Override
    public Ini.Section saveToPreferences(Ini prefs) {
        prefs.remove(storageKeyPrefix);
        Ini.Section section = prefs.add(storageKeyPrefix);
        section.put("position", GuiUtils.getPosition(this));
        section.put("visible", this.isVisible());
        return section;
    }

    @Override
    public Ini.Section loadFromPreferences(Ini prefs) {
        Ini.Section section = AppPreferences.getSection(prefs, storageKeyPrefix);
        // set position
        String positionStr = section.get("position");
        GuiUtils.setPosition(positionStr, this);
        // set visibility
        if (section.get("visible", Boolean.class, false)) {
            this.visibleOnInit = true;
        }
        return section;
    }

    public void setVisibleOnInit() {
        if (this.visibleOnInit) {
            this.setVisible(true);
        }
        this.visibleOnInit = false;
    }
}
