package net.parostroj.timetable.model;

import groovy.text.GStringTemplateEngine;
import groovy.text.Template;

import java.util.Map;

/**
 * MVEL text template.
 *
 * @author jub
 */
public final class TextTemplateGroovy extends TextTemplate {

    private final Template templateGString;
    
    protected TextTemplateGroovy(String template) throws GrafikonException {
        super(template);
        GStringTemplateEngine engine = new GStringTemplateEngine();
        try {
            templateGString = engine.createTemplate(template);
        } catch (Exception e) {
            throw new GrafikonException("Cannot create template: " + e.getMessage(), e, GrafikonException.Type.TEXT_TEMPLATE);
        }
    }

    @Override
    public String evaluate(Map<String, Object> binding) {
        return templateGString.make(binding).toString();
    }

    @Override
    public String evaluate(Object object, Map<String, Object> binding) {
        return this.evaluate(binding);
    }

    @Override
    public Language getLanguage() {
        return Language.GROOVY;
    }
}
