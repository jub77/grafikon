package net.parostroj.timetable.model;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Groovy text template.
 *
 * @author jub
 */
public final class TextTemplateGroovy extends TextTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(TextTemplateGroovy.class);

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
            LOG.warn(e.getMessage());
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
}
