package net.parostroj.timetable.gui.ini;

/**
 * Interface for loading/saving data about GUI.
 *
 * @author jub
 */
public interface StorableGuiData {

    public IniConfigSection saveToPreferences(IniConfig prefs);

    public IniConfigSection loadFromPreferences(IniConfig prefs);
}
