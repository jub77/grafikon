package net.parostroj.timetable.model;

import java.util.*;

/**
 * Localization.
 *
 * @author jub
 */
public class Localization {

    public static class Translation {
        private final Locale locale;
        private final String text;

        public Translation(Locale locale, String text) {
            this.locale = locale;
            this.text = text;
        }

        public Locale getLocale() {
            return locale;
        }

        public String getText() {
            return text;
        }

        /**
         * Equals method compares only locale.
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Translation other = (Translation) obj;
            if (locale == null) {
                if (other.locale != null)
                    return false;
            } else if (!locale.equals(other.locale))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return String.format("<%s,%s>", locale, text);
        }
    }

    private final Map<String, List<Translation>> texts;
    private final Set<Locale> locales;

    protected Localization() {
        texts = new HashMap<String, List<Translation>>();
        locales = new HashSet<Locale>();
    }

    public void addKey(String key) {
        if (!texts.containsKey(key)) {
            texts.put(key, null);
        }
    }

    public void removeKey(String key) {
        texts.remove(key);
    }

    public Collection<String> getKeys() {
        return Collections.unmodifiableCollection(texts.keySet());
    }

    public void addLocale(Locale locale) {
        locales.add(locale);
    }

    public void removeLocale(Locale locale) {
        if (locales.remove(locale)) {
            // remove translations with given locale
            for (String key : texts.keySet()) {
                this.removeTranslation(key, locale);
            }
        }
    }

    public Locale getLocale(String localeString) {
        for (Locale locale : locales) {
            if (locale.toString().equals(localeString)) {
                return locale;
            }
        }
        return null;
    }

    public Collection<Locale> getLocales() {
        return Collections.unmodifiableCollection(locales);
    }

    public void addTranslation(String key, Translation translation) {
        this.keyCheck(key);
        List<Translation> list = texts.get(key);
        if (list == null) {
            list = new LinkedList<Translation>();
            texts.put(key, list);
        }
        int index = list.indexOf(translation);
        if (index != -1) {
            list.set(index, translation);
        } else {
            list.add(translation);
        }
    }

    public void removeTranslation(String key, Locale locale) {
        this.keyCheck(key);
        List<Translation> list = texts.get(key);
        if (list != null) {
            list.remove(new Translation(locale, null));
            if (list.isEmpty()) {
                texts.put(key, null);
            }
        }
    }

    public Collection<Translation> getTranslations(String key) {
        List<Translation> list = texts.get(key);
        return list != null ? list : Collections.<Translation>emptyList();
    }

    public String translate(String key, Locale locale) {
        return this.translate(key, locale, null);
    }

    public String translate(String key, Locale locale, String defaultString) {
        String result = null;
        List<Translation> list = texts.get(key);
        if (list != null) {
            int index = list.indexOf(new Translation(locale, null));
            if (index != -1) {
                result = list.get(index).getText();
            }
        }
        result = result == null ? defaultString : result;
        return result != null ? result : key;
    }

    private void keyCheck(String key) {
        if (!texts.containsKey(key)) {
            throw new IllegalArgumentException("Unknown key: " + key);
        }
    }
}
