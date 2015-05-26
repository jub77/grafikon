package net.parostroj.timetable.gui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import net.parostroj.timetable.utils.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Languages utility
 *
 * @author jub
 */
public class LanguageLoader {

    private static final Logger log = LoggerFactory.getLogger(LanguageLoader.class);

    private final List<Pair<String, Locale>> languages;
    private Map<Locale, String> languagesMap;
    private List<Locale> locales;

    public LanguageLoader() {
        Properties properties = getLanguages();
        List<Pair<String, Locale>> list = new ArrayList<Pair<String, Locale>>(properties.size());
        for (Map.Entry<Object,Object> entry : properties.entrySet()) {
            Locale language = Locale.forLanguageTag((String) entry.getKey());
            String text = (String)entry.getValue();
            list.add(new Pair<String, Locale>(text, language));
        }
        languages = list;
    }

    private Properties getLanguages() {
        Properties langProps = new Properties();
        try (InputStream stream = LanguageLoader.class.getResourceAsStream("/languages.properties")) {
            langProps.load(stream);
        } catch (IOException e) {
            log.error("Error loading languages.", e);
        }
        return langProps;
    }

    public List<Locale> getLocales() {
        if (locales == null) {
            List<Pair<String,Locale>> list = getLocalesAndTexts();
            List<Locale> result = new ArrayList<Locale>(list.size());
            for (Pair<String, Locale> pair : list) {
                result.add(pair.second);
            }
            locales = result;
        }
        return locales;
    }

    public List<Pair<String, Locale>> getLocalesAndTexts() {
        return languages;
    }

    public List<Pair<String, Locale>> getLocalesAndTexts(String systemLocale) {
        List<Pair<String, Locale>> list = getLocalesAndTexts();
        List<Pair<String, Locale>> result = new ArrayList<Pair<String,Locale>>(list.size() + 1);
        result.add(new Pair<String, Locale>(systemLocale, null));
        result.addAll(list);
        return result;
    }

    public Map<Locale, String> getLocaleMap() {
        if (languagesMap == null) {
            List<Pair<String, Locale>> list = getLocalesAndTexts();
            Map<Locale, String> map = new HashMap<Locale, String>();
            for (Pair<String, Locale> pair : list) {
                map.put(pair.second, pair.first);
            }
            languagesMap = map;
        }
        return languagesMap;
    }

    public Map<Locale, String> getLocaleMap(String systemLocale) {
        Map<Locale, String> map = new HashMap<Locale, String>(this.getLocaleMap());
        map.put(null, systemLocale);
        return map;
    }
}
