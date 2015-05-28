package net.parostroj.timetable.output2;

import java.util.Locale;

/**
 * Translator interface for output translation.
 *
 * @author jub
 */
public interface Translator {

    /**
     * Translates text using given locale. If not found, it returns original text.
     *
     * @param text original text
     * @param locale locale
     * @return translated text
     */
    String translate(String text, Locale locale);

    /**
     * Returns predefined text for given location and key.
     *
     * @param key key
     * @param locale locale
     * @return localized text
     */
    String getText(String key, Locale locale);
}
