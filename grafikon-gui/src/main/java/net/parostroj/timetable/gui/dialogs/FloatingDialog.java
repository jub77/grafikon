package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;

import javax.swing.JComponent;

import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.utils.GuiUtils;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Floating dialog window.
 *
 * @author jub
 */
public class FloatingDialog extends javax.swing.JDialog implements FloatingWindow {

    private static final long serialVersionUID = 1L;

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
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        prefs.removeSection(storageKeyPrefix);
        IniConfigSection section = prefs.getSection(storageKeyPrefix);
        section.put("position", GuiUtils.getPosition(this));
        section.put("visible", this.isVisible());
        return section;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection(storageKeyPrefix);
        // set position
        String positionStr = section.get("position");
        GuiUtils.setPosition(positionStr, this);
        // set visibility
        if (section.get("visible", Boolean.class, false) == Boolean.TRUE) {
            this.visibleOnInit = true;
        }
        return section;
    }

    @Override
    public void setVisibleOnInit() {
        if (this.visibleOnInit) {
            this.setVisible(true);
        }
        this.visibleOnInit = false;
    }
}
