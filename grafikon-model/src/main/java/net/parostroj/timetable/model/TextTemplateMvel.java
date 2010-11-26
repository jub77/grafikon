package net.parostroj.timetable.model;

import java.util.Map;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MVEL text template.
 *
 * @author jub
 */
public final class TextTemplateMvel extends TextTemplate {
    
    private static final Logger LOG = LoggerFactory.getLogger(TextTemplateMvel.class);

    protected TextTemplateMvel(String template) {
        super(template);
    }

    @Override
    public String evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            return (String)TemplateRuntime.eval(getTemplate(), binding);
        } catch (Exception e) {
            throw new GrafikonException("Error evaluating template: " + e.getMessage(), e, GrafikonException.Type.TEXT_TEMPLATE);
        }
    }

    @Override
    public String evaluate(Map<String, Object> binding) {
        try {
            return this.evaluateWithException(binding);
        } catch (Exception e) {
            LOG.warn(e.getMessage());
            return "-- Template error --";
        }
    }

    @Override
    public Language getLanguage() {
        return Language.MVEL;
    }
}
