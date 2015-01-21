package net.parostroj.timetable.gui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import net.parostroj.timetable.utils.LocaleUtils;
import net.parostroj.timetable.utils.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Languages utility
 *
 * @author jub
 */
public class LanguagesUtil {

    private static final Logger log = LoggerFactory.getLogger(LanguagesUtil.class);

    public static Properties getLanguages() {
        Properties langProps = new Properties();
        try (InputStream stream = LanguagesUtil.class.getResourceAsStream("/languages.properties")) {
            langProps.load(stream);
        } catch (IOException e) {
            log.error("Error loading languages.", e);
        }
        return langProps;
    }

    public static List<Pair<String, Locale>> getLocales() {
        Properties languages = getLanguages();
        List<Pair<String, Locale>> result = new ArrayList<Pair<String, Locale>>(languages.size());
        for (Map.Entry<Object,Object> entry : languages.entrySet()) {
            Locale language = LocaleUtils.parseLocale((String)entry.getKey());
            String text = (String)entry.getValue();
            result.add(new Pair<String, Locale>(text, language));
        }
        return result;
    }

    public static Map<Locale, String> getLocaleMap() {
        List<Pair<String, Locale>> list = getLocales();
        Map<Locale, String> map = new HashMap<Locale, String>();
        for (Pair<String, Locale> l : list) {
            map.put(l.second, l.first);
        }
        return map;
    }
}
