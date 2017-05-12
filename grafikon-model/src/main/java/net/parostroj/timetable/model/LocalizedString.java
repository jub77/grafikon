package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * Localized string. It contains default text for not recognized locale and collection of text and
 * locale pairs.
 *
 * @author jub
 */
public interface LocalizedString extends TranslatedString {

    @Override
    default LocalizedString getNullIfEmpty() {
        return isDefaultStringEmpty() ? null : this;
    }

    /**
     * @return collection of text and locale pairs
     */
    Collection<StringWithLocale> getLocalizedStrings();

    @Override
    default Collection<Locale> getLocales() {
        return FluentIterable.from(getLocalizedStrings()).transform(str -> str.getLocale()).toList();
    }

    /**
     * @param locale locale
     * @return text for given locale or <code>null</code> if the locale is not present
     */
    default String getLocalizedString(final Locale locale) {
        StringWithLocale stringWithLocale = this.getLocalizedStringWithLocale(locale);
        return stringWithLocale == null ? null : stringWithLocale.getString();
    }

    /**
     * @param locale locale
     * @return string with locale or <code>null</code> if the locale is not present
     */
    default StringWithLocale getLocalizedStringWithLocale(final Locale locale) {
        if (locale != null) {
            Locale languageLocale = getOnlyLanguageLocale(locale);
            for (StringWithLocale localizedString : getLocalizedStrings()) {
                if (languageLocale.equals(localizedString.getLocale())) {
                    return localizedString;
                }
            }
        }
        return null;
    }

    @Override
    default String translate(Locale locale) {
        String translatedString = this.getLocalizedString(locale);
        return translatedString == null ? getDefaultString() : translatedString;
    }


    default String toCompleteString() {
        return toString();
    }

    /**
     * Text and locale pair.
     */
    interface StringWithLocale {
        String getString();
        Locale getLocale();
    }

    /**
     * Builder for localized string.
     */
    static class Builder {

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
            strings.add(new LocalizedStringImpl.StringWithLocaleImpl(string, getOnlyLanguageLocale(locale)));
        }

        public LocalizedString build() {
            if (defaultString == null) {
                throw new IllegalStateException("Default string missing");
            }
            // sort by language
            return new LocalizedStringImpl(defaultString,
                    ImmutableList.copyOf(getSortedStrings()));
        }

        private Collection<StringWithLocale> getSortedStrings() {
            if (strings == null) {
                return Collections.emptyList();
            } else {
                Collections.sort(strings, (s1, s2) -> s1.getLocale().toLanguageTag().compareTo(s2.getLocale().toLanguageTag()));
                return strings;
            }
        }
    }

    static Builder newBuilder() {
        return new Builder();
    }

    static Builder newBuilder(LocalizedString localizedString) {
        Builder builder = new Builder();
        if (localizedString != null) {
            builder.setDefaultString(localizedString.getDefaultString())
                    .addAllStringWithLocale(localizedString.getLocalizedStrings());
        }
        return builder;
    }

    static Builder newBuilder(String defaultString) {
        return new Builder().setDefaultString(defaultString);
    }

    static LocalizedString fromString(String text) {
        if (text == null) {
            return null;
        } else {
            return newBuilder(text).build();
        }
    }

    static StringWithLocale newStringWithLocale(String string, Locale locale) {
        return new LocalizedStringImpl.StringWithLocaleImpl(string, locale);
    }

    static StringWithLocale newStringWithLocale(String string, String locale) {
        return newStringWithLocale(string, Locale.forLanguageTag(locale));
    }

    static Locale getOnlyLanguageLocale(Locale locale) {
        if (locale.getCountry() == null) {
            return locale;
        } else {
            return Locale.forLanguageTag(locale.getLanguage());
        }
    }
}
