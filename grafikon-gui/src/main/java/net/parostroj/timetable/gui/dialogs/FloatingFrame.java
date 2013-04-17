package net.parostroj.timetable.gui.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.parostroj.timetable.gui.AppPreferences;
import net.parostroj.timetable.gui.utils.GuiUtils;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Floating dialog window.
 *
 * @author jub
 */
public class FloatingFrame extends javax.swing.JFrame implements FloatingWindow {

    private static final Dimension SIZE = new Dimension(400, 300);

    private final String storageKeyPrefix;
    private boolean visibleOnInit;

    public FloatingFrame(Frame parent, JComponent panel, String titleKey, String storageKeyPrefix) {
        super();
        if (titleKey != null)
            this.setTitle(ResourceLoader.getString(titleKey));
        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        this.storageKeyPrefix = storageKeyPrefix;

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setSize(SIZE); // initial size
    }


    protected String createStorageKey(String keySuffix) {
        return new StringBuilder(storageKeyPrefix).append('.').append(keySuffix).toString();
    }

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        boolean maximized = (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
        prefs.removeWithPrefix(storageKeyPrefix);
        prefs.setBoolean(this.createStorageKey("maximized"), maximized);
        prefs.setString(this.createStorageKey("position"), GuiUtils.getPositionFrame(this, maximized));
        prefs.setBoolean(this.createStorageKey("visible"), this.isVisible());

    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        String positionKey = this.createStorageKey("position");
        if (prefs.contains(positionKey)) {
            // set position
            String positionStr = prefs.getString(positionKey, null);
            GuiUtils.setPosition(positionStr, this);
        }
        if (prefs.getBoolean(this.createStorageKey("maximized"), false)) {
            // setting maximized state
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        // set visibility
        if (prefs.getBoolean(this.createStorageKey("visible"), false))
            this.visibleOnInit = true;
    }

    @Override
    public void setLocationRelativeTo(Component c) {
        setSize(SIZE);
        super.setLocationRelativeTo(c);
    }

    @Override
    public void setVisibleOnInit() {
        if (this.visibleOnInit)
            this.setVisible(true);
        this.visibleOnInit = false;
    }
}
