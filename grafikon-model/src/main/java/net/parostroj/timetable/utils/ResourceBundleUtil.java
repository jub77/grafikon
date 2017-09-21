package net.parostroj.timetable.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * Util for creating resource bundle with default language regardless of system default locale.
 *
 * @author jub
 */
public final class ResourceBundleUtil {

    private ResourceBundleUtil() {}

    /**
     * loads resource bundle.
     *
     * @param name name of the resource bundle
     * @param loader class loader
     * @param locale requested locale
     * @param defaultLocale default locale if the requested is not present
     * @return resource bundle
     */
    public static ResourceBundle getBundle(String name, ClassLoader loader, Locale locale, Locale defaultLocale) {
        Control control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);
        ResourceBundle bundle = ResourceBundle.getBundle(name, locale, loader, control);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(name, defaultLocale, loader);
        }
        return bundle;
    }
}
