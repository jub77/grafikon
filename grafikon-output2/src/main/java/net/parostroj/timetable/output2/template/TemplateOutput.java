package net.parostroj.timetable.output2.template;

import java.io.*;
import java.util.*;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.OutputWithLocale;

/**
 * Gsp output.
 *
 * @author jub
 */
public class TemplateOutput extends OutputWithLocale {

    private final TemplateWriterFactory templateWriterFactory;
    private final List<TemplateTransformerFactory> transformerFactories;

    public TemplateOutput(Locale locale, TemplateWriterFactory writerFactory, TemplateTransformerFactory... transformerFactories) {
        super(locale);
        this.templateWriterFactory = writerFactory;
        this.transformerFactories = Arrays.asList(transformerFactories);
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        Iterator<TemplateTransformerFactory> fi = transformerFactories.iterator();
        OutputStream os = getOutputStream(stream, !fi.hasNext());
        TemplateWriter template = templateWriterFactory.get();
        template.write(os, params, this.getLocale());
        while (fi.hasNext()) {
            InputStream is = getInputStreamFromTemp(os);
            TemplateTransformer transformer = fi.next().get();
            os = getOutputStream(stream, !fi.hasNext());
            transformer.process(is, os, params);
        }
    }

    private OutputStream getOutputStream(OutputStream finalOutputStream, boolean last) {
        return last ? finalOutputStream : new ByteArrayOutputStream();
    }

    private InputStream getInputStreamFromTemp(OutputStream os) {
        if (os instanceof ByteArrayOutputStream) {
            byte[] bytes = ((ByteArrayOutputStream) os).toByteArray();
            return new ByteArrayInputStream(bytes);
        } else {
            throw new IllegalArgumentException("Unexpected parameter: " + os);
        }
    }
}
