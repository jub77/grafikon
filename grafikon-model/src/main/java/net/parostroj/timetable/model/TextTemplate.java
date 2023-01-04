package net.parostroj.timetable.model;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Text template.
 *
 * @author jub
 */
public abstract class TextTemplate {

    public enum Language {
        GROOVY, PLAIN;

        public static Language fromString(String str) {
            for (Language language : values()) {
                if (language.name().equals(str)) {
                    return language;
                }
            }
            return null;
        }
    }

    private final String template;

    protected TextTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public abstract String evaluateWithException(Map<String, Object> binding) throws GrafikonException;

    public abstract String evaluate(Map<String, Object> binding);

    public void evaluate(OutputStream output, Map<String, Object> binding, String encoding) throws GrafikonException {
        try {
            this.evaluate(new OutputStreamWriter(output, encoding), binding);
        } catch (UnsupportedEncodingException e) {
            throw new GrafikonException("Error creating writer.", e);
        }
    }

    public abstract void evaluate(Writer output, Map<String, Object> binding) throws GrafikonException;

    public abstract Language getLanguage();

    public static Map<String, Object> getBinding(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static Map<String, Object> getBinding(Train train) {
        return train.getNameDelegate().createTemplateBinding();
    }

    public static Map<String, Object> getBinding(Train train, Locale locale) {
        return train.getNameDelegate().createTemplateBinding(locale);
    }

    public static TextTemplate createTextTemplate(String template, Language language) throws GrafikonException {
        return createTextTemplate(template, language, false);
    }

    public static TextTemplate createTextTemplate(String template, Language language, boolean initialize) throws GrafikonException {
        switch(language) {
            case GROOVY:
                return new TextTemplateGroovy(template, initialize);
            case PLAIN:
                return new TextTemplatePlain(template);
            default:
                throw new IllegalArgumentException("No template for language available.");
        }
    }

    public void freeResources() {
        // nothing
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TextTemplate other = (TextTemplate) obj;
        if (!Objects.equals(this.template, other.template)) {
            return false;
        }
        return this.getLanguage() == other.getLanguage();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.template != null ? this.template.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%s[%d]", getLanguage(), template != null ? template.length() : 0);
    }
}
