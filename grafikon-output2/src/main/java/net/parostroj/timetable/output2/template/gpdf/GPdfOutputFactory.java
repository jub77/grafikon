package net.parostroj.timetable.output2.template.gpdf;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.groovy.GroovyTemplateFactory;
import net.parostroj.timetable.output2.pdf.PdfTransformer;
import net.parostroj.timetable.output2.template.TemplateOutput;
import net.parostroj.timetable.output2.template.TemplateTransformerFactory;
import net.parostroj.timetable.output2.template.TemplateWriterFactory;

/**
 * Pdf output factory - groovy template to XSL-FO.
 *
 * @author jub
 */
public class GPdfOutputFactory extends OutputFactory {

    private static final String TYPE = "pdf.groovy";
    private static final List<String> OUTPUT_TYPES;

    private static final Logger log = LoggerFactory.getLogger(GPdfOutputFactory.class);

    static {
        OUTPUT_TYPES = Collections.unmodifiableList(Arrays.asList(
                "starts",
                "ends",
                "trains",
                "stations",
                "diagram",
                "engine_cycles",
                "custom_cycles",
                "train_unit_cycles"));
    }

    private final GroovyTemplateFactory factory;
    private final PdfTransformer transformer;

    public GPdfOutputFactory() {
        factory = new GroovyTemplateFactory();
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
            TemplateTransformerFactory transformerFactory = () -> (is, os, params) -> transformer.write(os, is,
                    getResolver(params));
            return new TemplateOutput(getLocale(), templateFactory, transformerFactory);
        } catch (Exception e) {
            throw new OutputException(e);
        }
    }

    private URIResolver getResolver(OutputParams params) {
        final OutputResources resources = this.getOutputResources(params);
        return (href, base) -> {
            InputStream is = resources != null ? resources.getStream(href) : null;
            Source streamSource = is == null ? null : new StreamSource(is);
            if (streamSource == null) {
                log.warn("Resource missing: {}", href);
            }
            return streamSource;
        };
    }

    private OutputResources getOutputResources(OutputParams params) {
        return params.getParamValue(Output.PARAM_RESOURCES, OutputResources.class);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
