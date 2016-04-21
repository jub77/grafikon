/*
 * ResourceLoader.java
 *
 * Created on 26.8.2007, 13:57:06
 */
package net.parostroj.timetable.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Wrapper for loading resources.
 *
 * @author jub
 */
public class ResourceLoader {

    /**
     * returns localized string for key.
     *
     * @param key key
     * @return localized string
     */
    public static String getString(String key) {
        try {
            return ResourceBundle.getBundle("gui").getString(key);
        } catch (MissingResourceException e) {
            // fall back to components resource file
            return net.parostroj.timetable.gui.utils.ResourceLoader.getString(key);
        }
    }
}
