package net.parostroj.timetable.gui.ini;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;

/**
 * Application preferences.
 *
 * @author jub
 */
public final class AppPreferences {

    private static final String PREFERENCES_NAME = ".grafikonrc";
    private static Ini instance = null;
    private static IniConfig configInstance = null;

    private AppPreferences() {}

    public static synchronized IniConfig getPreferences() throws IOException {
        if (configInstance == null) {
            instance = new Ini();
            instance.getConfig().setEscape(false);
            instance.getConfig().setEmptySection(true);
            load(instance);
            configInstance = new IniConfigIni4j(instance);
        }
        return configInstance;
    }

    public static synchronized void storePreferences() throws IOException {
        if (instance != null) {
            save(instance);
        }
    }

    private static void load(Ini ini) throws IOException {
        String homeDir = getSaveDirectory();
        if (homeDir != null) {
            File propsFile = new File(homeDir, PREFERENCES_NAME);
            if (propsFile.exists()) {
                ini.load(propsFile);
            }
        }
    }

    private static void save(Ini ini) throws IOException {
        String homeDir = getSaveDirectory();
        if (homeDir != null) {
            File propsFile = new File(homeDir, PREFERENCES_NAME);
            ini.store(propsFile);
        }
    }

    private static String getSaveDirectory() {
        return System.getProperty("user.home");
    }
}
