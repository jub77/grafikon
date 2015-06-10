package net.parostroj.timetable.output2.template.gsp;

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

    private static final String TYPE = "test.groovy";
    private static final List<String> OUTPUT_TYPES;

    static {
        OUTPUT_TYPES = Collections.unmodifiableList(Arrays.asList("starts", "trains", "diagram"));
    }

    private final GroovyTemplateFactory factory;

    public GspOutputFactory() {
        factory = new GroovyTemplateFactory();
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
            TemplateWriterFactory templateFactory = () -> factory.getTemplate(type, StandardCharsets.UTF_8);
            TemplateBindingHandler bindingFactory = factory.getBinding(type);
            return new TemplateOutput(getLocale(), templateFactory, bindingFactory);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
