package net.parostroj.timetable.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Simple text template - only simple variable substitution.
 *
 * @author jub
 */
public class TextTemplateSimple extends TextTemplate {

    TextTemplateSimple(String template) {
        super(template);
    }

    @Override
    public String evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        return getTemplate();
    }

    @Override
    public String evaluate(Map<String, Object> binding) {
        return getTemplate();
    }

    @Override
    public void evaluate(Writer output, Map<String, Object> binding) throws GrafikonException {
        try {
            output.write(getTemplate());
            output.flush();
        } catch (IOException e) {
            throw new GrafikonException("Error writing output.", e);
        }
    }

    @Override
    public Language getLanguage() {
        return Language.SIMPLE;
    }
}
