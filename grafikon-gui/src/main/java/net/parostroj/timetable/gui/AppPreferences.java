package net.parostroj.timetable.gui;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;

/**
 * Application preferences.
 *
 * @author jub
 */
public class AppPreferences {

    private static final String PREFERENCES_NAME = ".grafikonrc";
    private static Ini instance = null;

    public synchronized static Ini getPreferences() throws IOException {
        if (instance == null) {
            instance = new Ini();
            instance.getConfig().setEscape(false);
            instance.getConfig().setEmptySection(true);
            load(instance);
        }
        return instance;
    }

    public synchronized static Ini.Section getSection(String name) throws IOException {
        Ini ini = getPreferences();
        return getSection(ini, name);
    }

    public static Ini.Section getSection(Ini ini, String name) {
        if (!ini.containsKey(name)) {
            ini.add(name);
        }
        return ini.get(name);
    }

    public synchronized static void storePreferences() throws IOException {
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
