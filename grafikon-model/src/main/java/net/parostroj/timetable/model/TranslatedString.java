package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Locale;

/**
 * String that can be translated.
 *
 * @author jub
 */
public interface TranslatedString {
    /**
     * @return default text
     */
    String getDefaultString();

    /**
     * Optional operation - can return null if the information is not available
     *
     * @return collection of locales of this string
     */
    default Collection<Locale> getLocales() {
        return null;
    }

    /**
     * @return text translation for default locale
     */
    default String translate() {
        return translate(Locale.getDefault());
    }

    /**
     * @param locale locale
     * @return text for given locale or default text if the locale is not present
     */
    String translate(Locale locale);
}
