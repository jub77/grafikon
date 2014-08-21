/*
 * ResourceLoader.java
 *
 * Created on 26.8.2007, 13:57:06
 */
package net.parostroj.timetable.model.save;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for loading resources.
 *
 * @author jub
 */
public class ModelResourceLoader {

    private static final Logger log = LoggerFactory.getLogger(ModelResourceLoader.class);

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
            log.warn("Error getting text for key: {}", key);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
