package net.parostroj.timetable.output2.template.gsp;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputFactory;
import net.parostroj.timetable.output2.groovy.GroovyTemplateFactory;
import net.parostroj.timetable.output2.template.*;

/**
 * Html output factory - groovy.
 *
 * @author jub
 */
public class GspOutputFactory extends OutputFactory {

    private static final String TYPE = "groovy";
    private static final List<String> OUTPUT_TYPES;
    private static final String TEMPLATE_BASE_LOCATION = "templates/groovy/";

    static {
        OUTPUT_TYPES = Collections.unmodifiableList(Arrays.asList(
                "starts",
                "ends",
                "trains",
                "stations",
                "train_unit_cycles",
                "driver_cycles",
                "engine_cycles",
                "custom_cycles",
                "diagram"));
    }

    private final GroovyTemplateFactory factory;

    public GspOutputFactory() {
        factory = new GroovyTemplateFactory(TEMPLATE_BASE_LOCATION);
    }

    private Charset getCharset() {
        Charset charset = (Charset) this.getParameter("charset");
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        return charset;
    }

    private Locale getLocale() {
        Locale locale = (Locale) this.getParameter("locale");
        if (locale == null)
            locale = Locale.getDefault();
        return locale;
    }

    @Override
    public Collection<String> getOutputTypes() {
        return OUTPUT_TYPES;
    }

    @Override
    public Output createOutput(String type) throws OutputException {
        try {
            if (!OUTPUT_TYPES.contains(type)) {
                throw new OutputException("Unknown type: " + type);
            }
            TemplateWriterFactory templateFactory = () -> factory.getTemplate(type, this.getCharset());
            return new TemplateOutput(getLocale(), templateFactory);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
