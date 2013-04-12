package net.parostroj.timetable.gui.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for loading resources.
 *
 * @author jub
 */
public class ResourceLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class.getName());

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
            LOG.warn("Error getting text for key: {}", key);
            return "MISSING STRING FOR KEY: " + key;
        }
    }

    /**
     * @param icon icon type
     * @return icon
     */
    public static ImageIcon createImageIcon(GuiIcon icon) {
        return createImageIcon(icon.getPath());
    }

    /**
     * @param path path of the icon
     * @return icon
     */
    public static ImageIcon createImageIcon(String path) {
        return createImageIcon(path, null);
    }

    /**
     * @param path path of the icon
     * @param description icon description
     * @return icon
     */
    public static ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = ResourceLoader.class.getResource("/" + path);
        if (imgURL != null) {
            return description == null ? new ImageIcon(imgURL) : new ImageIcon(imgURL, description);
        } else {
            LOG.warn("Could not find icon: {}", path);
            return null;
        }
    }
}
