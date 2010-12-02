package net.parostroj.timetable.model;

import java.util.Map;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
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
    
    private CompiledTemplate compiledTemplate;

    protected TextTemplateMvel(String template) {
        super(template);
        TemplateCompiler compiler = new TemplateCompiler(template);
        compiledTemplate = compiler.compile();
    }

    @Override
    public String evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            return TemplateRuntime.execute(compiledTemplate, binding).toString();
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
