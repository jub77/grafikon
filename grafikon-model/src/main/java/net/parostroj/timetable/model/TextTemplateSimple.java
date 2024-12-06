package net.parostroj.timetable.model;

import net.parostroj.timetable.utils.SimpleSubstitutedString;

import java.util.Map;
import java.util.Objects;

/**
 * Simple text template - only simple variable substitution.
 *
 * @author jub
 */
public class TextTemplateSimple implements TextTemplate {

    private final String template;

    private SimpleSubstitutedString substitutedString;

    TextTemplateSimple(String template) {
        this.template = template;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public Language getLanguage() {
        return Language.SIMPLE;
    }

    @Override
    public String evaluate(Map<String, Object> binding) {
        if (substitutedString == null) {
            substitutedString = SimpleSubstitutedString.parse(template);
        }
        return substitutedString.substitute(binding);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TextTemplateSimple that = (TextTemplateSimple) o;
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
