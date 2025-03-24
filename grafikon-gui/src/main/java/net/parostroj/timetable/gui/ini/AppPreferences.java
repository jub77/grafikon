package net.parostroj.timetable.gui.ini;

import java.io.File;
import java.io.IOException;

/**
 * Application preferences.
 *
 * @author jub
 */
public final class AppPreferences {

    private static final String PREFERENCES_NAME = ".grafikonrc";
    private static IniConfig configInstance = null;

    private AppPreferences() {}

    public static synchronized IniConfig getPreferences() throws IOException {
        if (configInstance == null) {
            File iniFile = getIniFile();
            configInstance = iniFile != null ? new IniConfigIni4j(iniFile) : new IniConfigIni4j();
            configInstance.load();
        }
        return configInstance;
    }

    public static synchronized void storePreferences() throws IOException {
        if (configInstance != null) {
            configInstance.save();
        }
    }

    private static File getIniFile() {
        String homeDir = getSaveDirectory();
        if (homeDir != null) {
            return new File(homeDir, PREFERENCES_NAME);
        } else {
            return null;
        }
    }

    private static String getSaveDirectory() {
        return System.getProperty("user.home");
    }
}
