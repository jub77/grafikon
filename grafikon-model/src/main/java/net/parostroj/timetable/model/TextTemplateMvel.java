package net.parostroj.timetable.model;

import java.util.Map;
import org.mvel2.templates.TemplateRuntime;

/**
 * MVEL text template.
 *
 * @author jub
 */
public final class TextTemplateMvel extends TextTemplate {

    protected TextTemplateMvel(String template) {
        super(template);
    }

    @Override
    public String evaluate(Map<String, Object> binding) {
        return (String)TemplateRuntime.eval(getTemplate(), binding);
    }

    @Override
    public String evaluate(Object object, Map<String, Object> binding) {
        return this.evaluate(binding);
    }

    @Override
    public Language getLanguage() {
        return Language.MVEL;
    }
}
