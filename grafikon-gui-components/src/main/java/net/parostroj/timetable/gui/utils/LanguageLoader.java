package net.parostroj.timetable.gui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import net.parostroj.timetable.gui.pm.EnumeratedValuesPM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Languages utility
 *
 * @author jub
 */
public class LanguageLoader {

    public enum LanguagesType {
        GUI("locales.gui"), OUTPUT("locales.output");

        private String key;

        private LanguagesType(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private static final String LANGUAGES_PROPERTIES = "/languages.properties";

    private static final Logger log = LoggerFactory.getLogger(LanguageLoader.class);

    private final Map<LanguagesType, List<Locale>> locales;

    private static final LanguageLoader loader = new LanguageLoader();

    public static LanguageLoader getInstance() {
        return loader;
    }

    private LanguageLoader() {
        Properties properties = getLanguageProperties();
        locales = new EnumMap<>(LanguagesType.class);
        for (LanguagesType type : LanguagesType.values()) {
            List<Locale> ls = getLocales(properties, type.getKey());
            locales.put(type, ls);
        }
    }

    private List<Locale> getLocales(Properties properties, String key) {
        String property = properties.getProperty(key, "");
        List<Locale> locales = new ArrayList<>();
        for (String lang : property.split(",")) {
            Locale locale = Locale.forLanguageTag(lang);
            if (locale != null) {
                locales.add(locale);
            }
        }
        return locales;
    }

    private Properties getLanguageProperties() {
        Properties langProps = new Properties();
        try (InputStream stream = LanguageLoader.class.getResourceAsStream(LANGUAGES_PROPERTIES)) {
            langProps.load(stream);
        } catch (IOException e) {
            log.error("Error loading languages.", e);
        }
        return langProps;
    }

    public List<Locale> getLocales(LanguagesType type) {
        return locales.get(type);
    }

    public Map<Locale, String> createMap(List<Locale> locales) {
        return EnumeratedValuesPM.createValueMap(locales, l -> l.getDisplayName(l));
    }

    public Map<Locale, String> createMap(List<Locale> locales, String system) {
        return EnumeratedValuesPM.createValueMapWithNull(locales, l -> l == null ? system : l.getDisplayName(l));
    }
}
