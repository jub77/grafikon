package net.parostroj.timetable.output2.html.groovy;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.*;
import java.util.*;

import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.util.ResourceHelper;

/**
 * Gsp output.
 *
 * @author jub
 */
public abstract class GspOutput extends OutputWithLocale {

    public static final String TRANSLATOR = "translator";

    /** Cached default template. */
    private final Map<String, Template> _cachedTemplates;

    public GspOutput(Locale locale) {
        super(locale);
        _cachedTemplates = new HashMap<String, Template>();
    }

    protected Template getTemplate(OutputParams params, String defaultTemplate, ClassLoader classLoader) throws IOException {
        Template template = null;
        // load template if needed otherwise use cached
        if (_cachedTemplates.get(defaultTemplate) == null) {
            template = loadTemplate(params, defaultTemplate, classLoader);
            _cachedTemplates.put(defaultTemplate, template);
        } else {
            template = _cachedTemplates.get(defaultTemplate);
        }
        return template;
    }

    protected Template loadTemplate(OutputParams params, String defaultTemplate, ClassLoader classLoader) throws IOException {
        SimpleTemplateEngine ste = new SimpleTemplateEngine();
        try (InputStream is = ResourceHelper.getStream(defaultTemplate, classLoader)) {
            Template template = ste.createTemplate(new InputStreamReader(is, "utf-8"));
            return template;
        }
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
        map.put("diagram", params.getParam(DefaultOutputParam.TRAIN_DIAGRAM).getValue());
        map.put("locale", this.leaveOnlyLanguage(this.getLocale()));
        if (params.paramExistWithValue(DefaultOutputParam.CONTEXT)) {
            Map<?, ?> context = params.get(DefaultOutputParam.CONTEXT).getValue(Map.class);
            for (Map.Entry<?, ?> entry : context.entrySet()) {
                map.put((String) entry.getKey(), entry.getValue());
            }
        }
    }

    private Locale leaveOnlyLanguage(Locale locale) {
        return Locale.forLanguageTag(locale.getLanguage());
    }
}
