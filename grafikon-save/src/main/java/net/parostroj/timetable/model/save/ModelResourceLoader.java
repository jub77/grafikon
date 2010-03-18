/*
 * ResourceLoader.java
 * 
 * Created on 26.8.2007, 13:57:06
 */
package net.parostroj.timetable.model.save;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper for loading resources.
 * 
 * @author jub
 */
public class ModelResourceLoader {
    
    private static final Logger LOG = Logger.getLogger(ModelResourceLoader.class.getName());
    
    /**
     * returns localized string for key.
     * 
     * @param key key
     * @return localized string
     */
    public static String getString(String key) {
        try {
            return ResourceBundle.getBundle("model_texts").getString(key);
        } catch (MissingResourceException e) {
            LOG.log(Level.WARNING, "Error getting text for key: " + key, e);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
