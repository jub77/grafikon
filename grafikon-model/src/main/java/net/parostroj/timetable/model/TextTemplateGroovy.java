package net.parostroj.timetable.model;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Groovy text template.
 *
 * @author jub
 */
public final class TextTemplateGroovy extends TextTemplate {

    private static final Logger log = LoggerFactory.getLogger(TextTemplateGroovy.class);

    private Template templateGString;

    protected TextTemplateGroovy(String template, boolean initialize) throws GrafikonException {
        super(template);
        if (initialize)
            initialize();
    }

    private void initialize() throws GrafikonException {
        TemplateEngine engine = new SimpleTemplateEngine();
        try {
            templateGString = engine.createTemplate(this.getTemplate());
        } catch (Exception e) {
            throw new GrafikonException("Cannot create template: " + e.getMessage(), e, GrafikonException.Type.TEXT_TEMPLATE);
        }
    }

    @Override
    public String evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            if (templateGString == null)
                initialize();
            return templateGString.make(binding).toString();
        } catch (GrafikonException e) {
            throw e;
        } catch (Exception e) {
            throw new GrafikonException("Error evaluating template: " + e.getMessage(), e, GrafikonException.Type.TEXT_TEMPLATE);
        }
    }

    @Override
    public String evaluate(Map<String, Object> binding) {
        try {
            return this.evaluateWithException(binding);
        } catch (GrafikonException e) {
            log.warn(e.getMessage(), e);
            return "-- Template error --";
        }
    }

    @Override
    public Language getLanguage() {
        return Language.GROOVY;
    }

    @Override
    public void freeResources() {
        templateGString = null;
    }

    @Override
    public void evaluate(Writer output, Map<String, Object> binding) throws GrafikonException {
        Writable result = templateGString.make(binding);
        try {
            result.writeTo(output);
            output.flush();
        } catch (IOException e) {
            throw new GrafikonException("Error writing output.", e);
        }
    }
}
