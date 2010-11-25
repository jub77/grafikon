package net.parostroj.timetable.model;

import java.util.Map;

/**
 * Text template.
 *
 * @author jub
 */
public abstract class TextTemplate {

    private final String template;

    protected TextTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public abstract String evaluate(Map<String, Object> binding);

    public abstract String evaluate(Object object, Map<String, Object> binding);

    public abstract Language getLanguage();

    public static TextTemplate createTextTemplate(String template, Language language) throws GrafikonException {
        switch(language) {
            case MVEL:
                return new TextTemplateMvel(template);
            case GROOVY:
                return new TextTemplateGroovy(template);
            default:
                throw new IllegalArgumentException("No template for language available.");
        }
    }
}
