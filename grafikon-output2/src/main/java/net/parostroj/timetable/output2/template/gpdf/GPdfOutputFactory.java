package net.parostroj.timetable.output2.template.gpdf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.xml.transform.TransformerFactory;

import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputFactory;
import net.parostroj.timetable.output2.groovy.GroovyTemplateFactory;
import net.parostroj.timetable.output2.pdf.PdfTransformer;
import net.parostroj.timetable.output2.template.*;

/**
 * Html output factory - groovy.
 *
 * @author jub
 */
public class GPdfOutputFactory extends OutputFactory {

    private static final String TYPE = "pdf.groovy";
    private static final List<String> OUTPUT_TYPES;
    private static final String TEMPLATE_BASE_LOCATION = "templates/groovy-fo/";

    static {
        OUTPUT_TYPES = Collections.unmodifiableList(Arrays.asList("starts"));
    }

    private final GroovyTemplateFactory factory;
    private final PdfTransformer transformer;

    public GPdfOutputFactory() {
        factory = new GroovyTemplateFactory(TEMPLATE_BASE_LOCATION);
        transformer = new PdfTransformer(TransformerFactory.newInstance());
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
            TemplateTransformerFactory transformerFactory = () -> {
                return (is, os) -> transformer.write(os, is);
            };
            return new TemplateOutput(getLocale(), templateFactory, transformerFactory);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
