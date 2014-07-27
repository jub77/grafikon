package net.parostroj.timetable.gui;

import java.util.*;

import javax.swing.JRadioButtonMenuItem;

import net.parostroj.timetable.gui.utils.LanguagesUtil;
import net.parostroj.timetable.utils.Pair;

/**
 * Creates menu with languages.
 *
 * @author jub
 */
public class LanguageMenuBuilder {

    public static class LanguageMenuItem extends JRadioButtonMenuItem {

        private Locale language;

        public LanguageMenuItem(String text, Locale language) {
            super(text);
            this.language = language;
        }

        public Locale getLanguage() {
            return language;
        }

        public void setLanguage(Locale language) {
            this.language = language;
        }
    }

    public List<LanguageMenuItem> createLanguageMenuItems() {
        List<LanguageMenuItem> languages = new LinkedList<LanguageMenuItem>();

        // load languages
        List<Pair<String, Locale>> locales = LanguagesUtil.getLocales();
        for (Pair<String, Locale> locale : locales) {
            languages.add(new LanguageMenuItem(locale.first, locale.second));
        }
        return this.sort(languages);
    }

    private List<LanguageMenuItem> sort(List<LanguageMenuItem> items) {
        Collections.sort(items, new Comparator<LanguageMenuItem>() {
            @Override
            public int compare(LanguageMenuItem o1, LanguageMenuItem o2) {
                return o1.getLanguage().toString().compareTo(o2.getLanguage().toString());
            }
        });
        return items;
    }
}