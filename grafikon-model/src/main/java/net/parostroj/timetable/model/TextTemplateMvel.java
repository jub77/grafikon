package net.parostroj.timetable.model;

import java.io.*;
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

    protected TextTemplateMvel(String template, boolean initialize) {
        super(template);
        if (initialize)
            initialize();
    }

    private void initialize() {
        TemplateCompiler compiler = new TemplateCompiler(this.getTemplate());
        compiledTemplate = compiler.compile();
    }

    @Override
    public String evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            if (compiledTemplate == null)
                initialize();
            return TemplateRuntime.execute(compiledTemplate, binding).toString();
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void freeResources() {
        compiledTemplate = null;
    }

    @Override
    public void evaluate(Writer output, Map<String, Object> binding) throws GrafikonException {
        try {
            output.write(this.evaluateWithException(binding));
            output.flush();
        } catch (IOException e) {
            throw new GrafikonException("Error writing output.", e);
        }
    }
}
