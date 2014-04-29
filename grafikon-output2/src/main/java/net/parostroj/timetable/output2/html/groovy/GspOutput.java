package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.*;
import java.util.*;

import net.parostroj.timetable.output2.*;

/**
 * Gsp output.
 *
 * @author jub
 */
public abstract class GspOutput extends OutputWithLocale {

    /** Cached default template. */
    private final Map<String, Template> _cachedTemplates;

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

    protected void writeOutput(OutputStream stream, Template template, Map<String, Object> binding, String encoding) throws OutputException {
        Writable result = template.make(binding);
        Writer writer;
        try {
            writer = new OutputStreamWriter(stream, encoding);
            result.writeTo(writer);
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            throw new OutputException("Error creating output.", e);
        } catch (IOException e) {
            throw new OutputException("Error writing output.", e);
        }
    }

    protected void addContext(OutputParams params, Map<String, Object> map) {
        if (params.paramExistWithValue(DefaultOutputParam.CONTEXT)) {
            Map<?, ?> context = params.get(DefaultOutputParam.CONTEXT).getValue(Map.class);
            for (Map.Entry<?, ?> entry : context.entrySet()) {
                map.put((String) entry.getKey(), entry.getValue());
            }
        }

    }
}
