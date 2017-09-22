package net.parostroj.timetable.gui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.*;

import net.parostroj.timetable.actions.ConvertComparator;
import net.parostroj.timetable.gui.pm.EnumeratedValuesPM;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Languages utility
 *
 * @author jub
 */
public class LanguageLoader {

    public enum LanguagesType {
        GUI("locales.gui");

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
    private final List<Locale> availableLocales;

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
        availableLocales = this.initAvailableLocales();
    }

    private List<Locale> getLocales(Properties properties, String key) {
        String property = properties.getProperty(key, "");
        List<Locale> loadedLocales = new ArrayList<>();
        for (String lang : property.split(",")) {
            Locale locale = Locale.forLanguageTag(lang);
            if (locale != null) {
                loadedLocales.add(locale);
            }
        }
        return loadedLocales;
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

    private List<Locale> initAvailableLocales() {
        List<Locale> available = new ArrayList<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            if (!ObjectsUtil.isEmpty(locale.getLanguage()) && ObjectsUtil.isEmpty(locale.getCountry())) {
                // only language (and no country)
                available.add(locale);
            }
        }
        Collections.sort(available, new ConvertComparator<>(Collator.getInstance(), e -> e.getDisplayName()));
        return available;
    }

    public List<Locale> getLocales(LanguagesType type) {
        return locales.get(type);
    }

    public List<Locale> getAvailableLocales() {
        return availableLocales;
    }

    public Map<Locale, String> createMap(Collection<Locale> locales) {
        return EnumeratedValuesPM.createValueMap(locales, l -> l.getDisplayName(l));
    }

    public Map<Locale, String> createMap(Collection<Locale> locales, String system) {
        return EnumeratedValuesPM.createValueMapWithNull(locales, l -> l == null ? system : l.getDisplayName(l));
    }

    public List<Wrapper<Locale>> createWrappers(List<Locale> locales, String system) {
        List<Wrapper<Locale>> wrappers = new ArrayList<>(locales.size() + 1);
        if (system != null) {
            wrappers.add(Wrapper.getEmptyWrapper(system));
        }
        wrappers.addAll(Wrapper.getWrapperList(locales));
        return wrappers;
    }
}
