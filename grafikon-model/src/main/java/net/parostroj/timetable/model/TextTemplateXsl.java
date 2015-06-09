package net.parostroj.timetable.model;

import java.io.*;
import java.util.Map;

/**
 * Xsl text template - uses xsl transformation. Input as a stream/string in binding.
 *
 * @author jub
 */
public class TextTemplateXsl extends TextTemplate {

    protected TextTemplateXsl(String template, boolean initialize) {
        super(template);
        if (initialize) {
            // initialization
        }
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
        return Language.XSL;
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
