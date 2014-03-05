package net.parostroj.timetable.gui.dialogs;

import java.awt.Frame;

import javax.swing.JComponent;

import net.parostroj.timetable.gui.AppPreferences;
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


    protected String createStorageKey(String keySuffix) {
        return new StringBuilder(storageKeyPrefix).append('.').append(keySuffix).toString();
    }

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        prefs.removeWithPrefix(storageKeyPrefix);
        prefs.setString(this.createStorageKey("position"), GuiUtils.getPosition(this));
        prefs.setBoolean(this.createStorageKey("visible"), this.isVisible());
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        // set position
        String positionStr = prefs.getString(this.createStorageKey("position"), null);
        GuiUtils.setPosition(positionStr, this);
        // set visibility
        if (prefs.getBoolean(this.createStorageKey("visible"), false))
                this.visibleOnInit = true;
    }

    public void setVisibleOnInit() {
        if (this.visibleOnInit)
            this.setVisible(true);
        this.visibleOnInit = false;
    }
}
