package net.parostroj.timetable.gui.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper for loading resources.
 * 
 * @author jub
 */
public class ResourceLoader {
    
    private static final Logger LOG = Logger.getLogger(ResourceLoader.class.getName());
    
    /**
     * returns localized string for key.
     * 
     * @param key key
     * @return localized string
     */
    public static String getString(String key) {
        try {
            return ResourceBundle.getBundle("net.parostroj.timetable.gui.components_texts").getString(key);
        } catch (MissingResourceException e) {
            LOG.log(Level.WARNING, "Error getting text for key: " + key, e);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
