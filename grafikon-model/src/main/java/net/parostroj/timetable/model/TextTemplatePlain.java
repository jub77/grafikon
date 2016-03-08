package net.parostroj.timetable.model;

import java.io.*;
import java.util.Map;

/**
 * Plain text template - return template string without any modification.
 * 
 * @author jub
 */
public class TextTemplatePlain extends TextTemplate {

    protected TextTemplatePlain(String template) {
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
    public Language getLanguage() {
        return Language.PLAIN;
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
}
