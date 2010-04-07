package net.parostroj.timetable.output2.html.groovy;

import java.io.IOException;
import java.io.InputStream;
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

    protected InputStream getTemplateStream(OutputParams params, String defaultTemplate) throws IOException {
        if (params.containsKey(DefaultOutputParam.TEMPLATE_STREAM)) {
            return (InputStream)params.getParam(DefaultOutputParam.TEMPLATE_STREAM).getValue();
        } else {
            return getClass().getResource(defaultTemplate).openStream();
        }
    }
}
