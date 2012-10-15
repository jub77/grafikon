package net.parostroj.timetable.gui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Application preferences.
 *
 * @author jub
 */
public class AppPreferences {

    private static final String PREFERENCES_NAME = ".grafikon";
    private static AppPreferences instance = null;
    private Properties preferencesProps = new Properties();

    /**
     * returns preferences for the application.
     * 
     * @return preferences
     * @throws java.io.IOException
     */
    public synchronized static AppPreferences getPreferences() throws IOException {
        if (instance == null) {
            instance = new AppPreferences();
            instance.load();
        }
        return instance;
    }

    public String getString(String key, String defaultValue) {
        String value = preferencesProps.getProperty(key);
        return value != null ? value : defaultValue;
    }

    public void setString(String key, String value) {
        preferencesProps.setProperty(key, value);
    }

    public void setInt(String key, Integer value) {
        preferencesProps.setProperty(key, value.toString());
    }

    public int getInt(String key, int defaultValue) {
        String value = this.getString(key, null);
        if (value == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    public void setBoolean(String key, Boolean value) {
        preferencesProps.setProperty(key, value.toString());
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = this.getString(key, null);
        if (value == null) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    public void remove(String key) {
        preferencesProps.remove(key);
    }

    public boolean contains(String key) {
        return preferencesProps.containsKey(key);
    }

    public void removeWithPrefix(String keyPrefix) {
        Set<String> removedKeys = null;
        for (Object obj : preferencesProps.keySet()) {
            String key = (String)obj;
            if (key.startsWith(keyPrefix)) {
                if (removedKeys == null)
                    removedKeys = new HashSet<String>();
                removedKeys.add(key);
            }
        }
        if (removedKeys != null)
            for (String key : removedKeys)
                preferencesProps.remove(key);
    }

    /**
     * loads preferences.
     * 
     * @throws java.io.IOException
     */
    public void load() throws IOException {
        String homeDir = this.getSaveDirectory();
        if (homeDir != null) {
            File propsFile = new File(homeDir, PREFERENCES_NAME);
            if (propsFile.exists()) {
                FileReader reader = new FileReader(propsFile);
                try {
                    preferencesProps.load(reader);
                } finally {
                    reader.close();
                }
            }
        }
    }

    /**
     * saves preferences.
     * 
     * @throws java.io.IOException
     */
    public void save() throws IOException {
        String homeDir = this.getSaveDirectory();
        if (homeDir != null) {
            Properties savedProperties = new Properties() {
                @Override
                public Set<Object> keySet() {
                    return new TreeSet<Object>(super.keySet());
                }

                @Override
                public synchronized Enumeration<Object> keys() {
                    final Iterator<Object> iterator = keySet().iterator();
                    return new Enumeration<Object>() {

                        @Override
                        public boolean hasMoreElements() {
                            return iterator.hasNext();
                        }

                        @Override
                        public Object nextElement() {
                            return iterator.next();
                        }
                    };
                }


            };
            for (String key : preferencesProps.stringPropertyNames()) {
                savedProperties.put(key, preferencesProps.getProperty(key));
            }
            File propsFile = new File(homeDir, PREFERENCES_NAME);
            FileWriter writer = new FileWriter(propsFile);
            try {
                savedProperties.store(writer, null);
            } finally {
                writer.close();
            }
        }
    }

    private String getSaveDirectory() {
        return System.getProperty("user.home");
    }
}
