package net.parostroj.timetable.gui.ini;

/**
 * Interface for loading/saving data about GUI.
 *
 * @author jub
 */
public interface StorableGuiData {

    IniConfigSection saveToPreferences(IniConfig prefs);

    IniConfigSection loadFromPreferences(IniConfig prefs);
}
