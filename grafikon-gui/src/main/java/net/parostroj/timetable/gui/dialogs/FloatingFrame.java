package net.parostroj.timetable.gui.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.utils.GuiUtils;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Floating dialog window.
 *
 * @author jub
 */
public class FloatingFrame extends javax.swing.JFrame implements FloatingWindow {

    private static final long serialVersionUID = 1L;

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


    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        prefs.removeSection(storageKeyPrefix);
        IniConfigSection section = prefs.getSection(storageKeyPrefix);
        boolean maximized = (this.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
        section.put("maximized", maximized);
        section.put("position", GuiUtils.getPositionFrame(this, maximized));
        section.put("visible", this.isVisible());
        return section;
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        IniConfigSection section = prefs.getSection(storageKeyPrefix);
        String positionStr = section.get("position");
        if (positionStr != null) {
            // set position
            GuiUtils.setPosition(positionStr, this);
        }
        if (section.get("maximized", Boolean.class, false)) {
            // setting maximized state
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        // set visibility
        if (section.get("visible", Boolean.class, false)) {
            this.visibleOnInit = true;
        }
        return section;
    }

    @Override
    public void setLocationRelativeTo(Component c) {
        setSize(SIZE);
        super.setLocationRelativeTo(c);
        this.requestFocus();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.requestFocus();
    }

    @Override
    public void setVisibleOnInit() {
        if (this.visibleOnInit)
            this.setVisible(true);
        this.visibleOnInit = false;
    }
}
