package net.parostroj.timetable.output2.html.groovy;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import net.parostroj.timetable.output2.DefaultOutputParam;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithLocale;

/**
 * Gsp output.
 *
 * @author jub
 */
public abstract class GspOutput extends OutputWithLocale {

    public GspOutput(Locale locale) {
        super(locale);
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

    protected Template createTemplate(OutputParams params, String defaultTemplate, ClassLoader classLoader) throws IOException {
        SimpleTemplateEngine ste = new SimpleTemplateEngine();
        InputStream is = getTemplateStream(params, defaultTemplate, classLoader);
        Template template = ste.createTemplate(new InputStreamReader(is, "utf-8"));
        is.close();
        return template;
    }
}
