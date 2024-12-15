package net.parostroj.timetable.model;

import java.io.*;
import java.util.Map;

/**
 * Text template.
 *
 * @author jub
 */
public interface TextTemplate {

    enum Language {
        SIMPLE, GROOVY, PLAIN;

        public static Language fromString(String str) {
            for (Language language : values()) {
                if (language.name().equals(str)) {
                    return language;
                }
            }
            return null;
        }
    }

    String getTemplate();

    default String evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        return evaluate(binding);
    }

    String evaluate(Map<String, Object> binding);

    default void evaluate(OutputStream output, Map<String, Object> binding, String encoding) throws GrafikonException {
        try {
            this.evaluate(new OutputStreamWriter(output, encoding), binding);
        } catch (UnsupportedEncodingException e) {
            throw new GrafikonException("Error creating writer.", e);
        }
    }

    default void evaluate(Writer output, Map<String, Object> binding) throws GrafikonException {
        try {
            output.write(evaluateWithException(binding));
            output.flush();
        } catch (IOException e) {
            throw new GrafikonException("Error writing output.", e);
        }
    }

    Language getLanguage();

    static TextTemplate create(String template, Language language) {
        return switch (language) {
            case GROOVY -> new TextTemplateGroovy(template);
            case PLAIN -> new TextTemplatePlain(template);
            case SIMPLE -> new TextTemplateSimple(template);
        };
    }
}
