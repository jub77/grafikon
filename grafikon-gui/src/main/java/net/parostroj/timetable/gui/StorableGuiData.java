package net.parostroj.timetable.gui;

import org.ini4j.Ini;

/**
 * Interface for loading/saving data about GUI.
 *
 * @author jub
 */
public interface StorableGuiData {

    public Ini.Section saveToPreferences(Ini prefs);

    public Ini.Section loadFromPreferences(Ini prefs);
}
