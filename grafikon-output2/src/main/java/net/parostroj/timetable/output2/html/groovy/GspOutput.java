package net.parostroj.timetable.output2.html.groovy;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.output2.DefaultOutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithLocale;

/**
 * Gsp output.
 *
 * @author jub
 */
public abstract class GspOutput extends OutputWithLocale {

    /** Cached default template. */
    private Map<String, Template> _cachedTemplates;

    public GspOutput(Locale locale) {
        super(locale);
        _cachedTemplates = new HashMap<String, Template>();
    }

    protected InputStream getTemplateStream(OutputParams params, String defaultTemplate, ClassLoader classLoader) throws IOException {
        if (params.containsKey(DefaultOutputParam.TEMPLATE_STREAM)) {
            return (InputStream)params.getParam(DefaultOutputParam.TEMPLATE_STREAM).getValue();
        } else {
            if (classLoader != null)
                return classLoader.getResourceAsStream(defaultTemplate);
            else
                return ClassLoader.getSystemResourceAsStream(defaultTemplate);
        }
    }

    protected Template getTemplate(OutputParams params, String defaultTemplate, ClassLoader classLoader) throws IOException {
        boolean templateInStream = params.containsKey(DefaultOutputParam.TEMPLATE_STREAM);
        Template template = null;
        // load template if needed otherwise use cached
        if (templateInStream || _cachedTemplates.get(defaultTemplate) == null) {
            template = loadTemplate(params, defaultTemplate, classLoader);
            if (!templateInStream)
                _cachedTemplates.put(defaultTemplate, template);
        } else {
            template = _cachedTemplates.get(defaultTemplate);
        }
        return template;
    }

    protected Template loadTemplate(OutputParams params, String defaultTemplate, ClassLoader classLoader) throws IOException {
        SimpleTemplateEngine ste = new SimpleTemplateEngine();
        InputStream is = getTemplateStream(params, defaultTemplate, classLoader);
        Template template = null;
        try {
            template = ste.createTemplate(new InputStreamReader(is, "utf-8"));
        } finally {
            is.close();
        }
        return template;
    }
}
