package net.parostroj.timetable.utils;

import java.util.Locale;

/**
 * Locale utility methods.
 *
 * @author jub
 */
public class LocaleUtils {

    public static Locale parseLocale(String localeString) {
        Locale returnedLocale = null;
        if (localeString != null) {
            String parts[] = localeString.split("_");
            if (parts.length == 1) {
                returnedLocale = new Locale(parts[0]);
            } else if (parts.length == 2) {
                returnedLocale = new Locale(parts[0],parts[1]);
            } else if (parts.length == 3) {
                returnedLocale = new Locale(parts[0], parts[1], parts[2]);
            }
        }
        return returnedLocale;
    }
}
