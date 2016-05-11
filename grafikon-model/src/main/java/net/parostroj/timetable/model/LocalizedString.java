package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Localized string. It contains default text for not recognized locale and collection of text and
 * locale pairs.
 *
 * @author jub
 */
public class LocalizedString {

    private final String defaultString;
    private final Collection<StringWithLocale> localizedStrings;

    private LocalizedString(String defaultString, List<StringWithLocale> localizedStrings) {
        this.defaultString = defaultString;
        this.localizedStrings = localizedStrings;
    }

    /**
     * @return default text
     */
    public String getDefaultString() {
        return defaultString;
    }

    /**
     * @return collection of text and locale pairs
     */
    public Collection<StringWithLocale> getLocalizedStrings() {
        return localizedStrings;
    }

    /**
     * @return collection of locales of this string
     */
    public Collection<Locale> getLocales() {
        return FluentIterable.from(localizedStrings).transform(str -> str.getLocale()).toList();
    }

    /**
     * @param locale locale
     * @return text for given locale or <code>null</null> if the locale is not present
     */
    public String getLocalizedString(final Locale locale) {
        Locale languageLocale = checkOnlyLanguage(locale);
        for (StringWithLocale localizedString : localizedStrings) {
            if (languageLocale.equals(localizedString.getLocale())) {
                return localizedString.getString();
            }
        }
        return null;
    }

    /**
     * @param locale locale
     * @return text for given locale or default text if the locale is not present
     */
    public String translate(Locale locale) {
        String translatedString = this.getLocalizedString(locale);
        return translatedString == null ? defaultString : translatedString;
    }

    @Override
    public String toString() {
        return String.format("i18n:%s[%s]", defaultString,
                String.join(",", Iterables.transform(localizedStrings, item -> item.getLocale().toLanguageTag())));
    }

    /**
     * Text and locale pair.
     */
    public interface StringWithLocale {
        String getString();
        Locale getLocale();
    }

    final static class StringWithLocaleImpl implements StringWithLocale {

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
    }

    /**
     * Builder for localized string.
     */
    public static class Builder {

        private String defaultString;
        private List<StringWithLocale> strings;

        private Builder() {
        }

        public Builder setDefaultString(String defaultString) {
            this.defaultString = defaultString;
            return this;
        }

        public Builder addStringWithLocale(String string, Locale locale) {
            this.addString(string, locale);
            return this;
        }

        public Builder addStringWithLocale(String string, String locale) {
            this.addString(string, Locale.forLanguageTag(locale));
            return this;
        }

        public Builder addStringWithLocale(StringWithLocale stringWithLocale) {
            this.addString(stringWithLocale.getString(), stringWithLocale.getLocale());
            return this;
        }

        public Builder addAllStringWithLocale(Collection<? extends StringWithLocale> collection) {
            for (StringWithLocale item : collection) {
                this.addString(item.getString(), item.getLocale());
            }
            return this;
        }

        private void addString(String string, Locale locale) {
            if (string == null || locale == null) {
                throw new IllegalArgumentException("Parameters cannot be null");
            }
            if (strings == null) {
                strings = new ArrayList<>();
            }
            strings.add(new StringWithLocaleImpl(string, checkOnlyLanguage(locale)));
        }

        public LocalizedString build() {
            if (defaultString == null) {
                throw new IllegalStateException("Default string missing");
            }
            return new LocalizedString(defaultString,
                    ImmutableList.copyOf(strings != null ? strings : Collections.emptyList()));
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(LocalizedString localizedString) {
        Builder builder = new Builder();
        if (localizedString != null) {
            builder.setDefaultString(localizedString.getDefaultString())
                    .addAllStringWithLocale(localizedString.getLocalizedStrings());
        }
        return builder;
    }

    public static Builder newBuilder(String defaultString) {
        return new Builder().setDefaultString(defaultString);
    }

    public static StringWithLocale newStringWithLocale(String string, Locale locale) {
        return new StringWithLocaleImpl(string, locale);
    }

    private static Locale checkOnlyLanguage(Locale locale) {
        if (locale.getCountry() == null) {
            return locale;
        } else {
            return Locale.forLanguageTag(locale.getLanguage());
        }
    }
}
