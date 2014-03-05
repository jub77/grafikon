package net.parostroj.timetable.gui;

/**
 * Interface for loading/saving data about GUI.
 * 
 * @author jub
 */
public interface StorableGuiData {
    public void saveToPreferences(AppPreferences prefs);

    public void loadFromPreferences(AppPreferences prefs);
}
