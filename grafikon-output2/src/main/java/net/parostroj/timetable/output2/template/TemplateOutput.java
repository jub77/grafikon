package net.parostroj.timetable.output2.template;

import java.io.OutputStream;
import java.util.Locale;

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

    public TemplateOutput(Locale locale, TemplateWriterFactory defaultTemplateFactory) {
        super(locale);
        this.templateWriterFactory = defaultTemplateFactory;
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        TemplateWriter template = templateWriterFactory.get();
        template.write(stream, params, diagram, this.getLocale());
    }
}
