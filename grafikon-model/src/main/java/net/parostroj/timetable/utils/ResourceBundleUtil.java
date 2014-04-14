package net.parostroj.timetable.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class ResourceBundleUtil {

    public static ResourceBundle getBundle(String name, ClassLoader loader, Locale locale, Locale defaultLocale) {
        Control control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);
        ResourceBundle bundle = ResourceBundle.getBundle(name, locale, loader, control);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(name, defaultLocale, loader);
        }
        return bundle;
    }
}
