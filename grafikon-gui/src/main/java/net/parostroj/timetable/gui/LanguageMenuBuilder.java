package net.parostroj.timetable.gui;

import java.util.*;

import javax.swing.JRadioButtonMenuItem;

import net.parostroj.timetable.gui.utils.LanguageLoader;
import net.parostroj.timetable.gui.utils.LanguageLoader.LanguagesType;
import net.parostroj.timetable.utils.Pair;

/**
 * Creates menu with languages.
 *
 * @author jub
 */
public class LanguageMenuBuilder {

    private final LanguageLoader languageLoader;

    public LanguageMenuBuilder(LanguageLoader languageLoader) {
        this.languageLoader = languageLoader;
    }

    public List<Pair<JRadioButtonMenuItem, Locale>> createLanguageMenuItems(String systemLanguage, LanguagesType type) {
        List<Pair<JRadioButtonMenuItem, Locale>> languages = new ArrayList<Pair<JRadioButtonMenuItem, Locale>>();

        // load languages
        Map<Locale, String> lMap = languageLoader.createMap(languageLoader.getLocales(type), systemLanguage);
        for (Map.Entry<Locale, String> locale : lMap.entrySet()) {
            languages.add(new Pair<JRadioButtonMenuItem, Locale>(new JRadioButtonMenuItem(locale.getValue()), locale.getKey()));
        }
        return this.sort(languages);
    }

    private List<Pair<JRadioButtonMenuItem, Locale>> sort(List<Pair<JRadioButtonMenuItem, Locale>> items) {
        Collections.sort(items, new Comparator<Pair<JRadioButtonMenuItem, Locale>>() {
            @Override
            public int compare(Pair<JRadioButtonMenuItem, Locale> o1, Pair<JRadioButtonMenuItem, Locale> o2) {
                return o1.second == null ? (o2.second == null ? 0 : -1) : (o2.second == null ? 1 : o1.second.toString()
                        .compareTo(o2.second.toString()));
            }
        });
        return items;
    }
}
