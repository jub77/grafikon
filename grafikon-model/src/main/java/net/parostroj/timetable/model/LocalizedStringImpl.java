package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Iterables;
import java.util.Objects;

/**
 * Localized string implementation.
 *
 * @author jub
 */
class LocalizedStringImpl implements LocalizedString {

    private final String defaultString;
    private final Collection<StringWithLocale> localizedStrings;

    LocalizedStringImpl(String defaultString, List<StringWithLocale> localizedStrings) {
        this.defaultString = defaultString;
        this.localizedStrings = localizedStrings;
    }

    /**
     * @return default text
     */
    @Override
    public String getDefaultString() {
        return defaultString;
    }

    /**
     * @return collection of text and locale pairs
     */
    @Override
    public Collection<StringWithLocale> getLocalizedStrings() {
        return localizedStrings;
    }

    @Override
    public String toString() {
        return String.format("i18n:%s[%s]", defaultString,
                String.join(",", Iterables.transform(localizedStrings, item -> item.getLocale().toLanguageTag())));
    }

    @Override
    public String toCompleteString() {
        return String.format("i18n:%s[%s]", defaultString, String.join(",", Iterables.transform(localizedStrings,
                item -> String.format("%s=%s", item.getLocale().toLanguageTag(), item.getString()))));
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultString, localizedStrings);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LocalizedStringImpl ls)) return false;
        if (!defaultString.equals(ls.defaultString)) return false;
        Iterator<StringWithLocale> i = localizedStrings.iterator();
        Iterator<StringWithLocale> li = ls.localizedStrings.iterator();
        while (i.hasNext() && li.hasNext()) {
            if (!i.next().equals(li.next())) return false;
        }
        return !i.hasNext() && !li.hasNext();
    }

    static final class StringWithLocaleImpl implements StringWithLocale {

        private final String string;
        private final Locale locale;

        public StringWithLocaleImpl(String string, Locale locale) {
            this.string = string;
            this.locale = locale;
        }

        @Override
        public String getString() {
            return string;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        @Override
        public int hashCode() {
            return Objects.hash(string, locale);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StringWithLocale str)) return false;
            return locale.equals(str.getLocale()) && string.equals(str.getString());
        }
    }
}
