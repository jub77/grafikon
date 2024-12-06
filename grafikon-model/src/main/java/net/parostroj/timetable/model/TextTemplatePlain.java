package net.parostroj.timetable.model;

import java.util.Map;
import java.util.Objects;

/**
 * Plain text template - return template string without any modification.
 *
 * @author jub
 */
public final class TextTemplatePlain implements TextTemplate {

    private final String template;

    TextTemplatePlain(String template) {
        this.template = template;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public Language getLanguage() {
        return Language.PLAIN;
    }

    @Override
    public String evaluate(Map<String, Object> binding) {
        return getTemplate();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TextTemplatePlain that = (TextTemplatePlain) o;
        return Objects.equals(template, that.template);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(template);
    }

    @Override
    public String toString() {
        return String.format("%s[%d]", getLanguage(), template != null ? template.length() : 0);
    }
}
