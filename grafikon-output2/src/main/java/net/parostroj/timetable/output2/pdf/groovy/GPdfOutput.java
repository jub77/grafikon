package net.parostroj.timetable.output2.pdf.groovy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.TextTemplate.Language;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.pdf.PdfTransformer;
import net.parostroj.timetable.output2.util.ResourceHelper;

public abstract class GPdfOutput extends OutputWithDiagramStream {

    static final Collection<String> NO_COPY = Collections.unmodifiableCollection(Arrays.asList(
            PARAM_TEMPLATE,
            PARAM_OUTPUT_FILE,
            PARAM_OUTPUT_STREAM));

    private final Output output;
    private final PdfTransformer transformer;

    public GPdfOutput(Output output, PdfTransformer transformer) {
        this.output = output;
        this.transformer = transformer;
    }

    protected OutputParams createGroovyParams(OutputParams params, String name, ClassLoader classLoader) throws OutputException {
        try {
            OutputParams groovyParams = new OutputParams();
            TextTemplate template = TextTemplate.createTextTemplate(ResourceHelper.readResource(name, classLoader),
                    Language.GROOVY, true);
            groovyParams.setParam(PARAM_TEMPLATE, template);
            this.copyParams(params, groovyParams);
            return groovyParams;
        } catch (GrafikonException e) {
            throw new OutputException("Error creating template", e);
        }
    }

    protected void copyParams(OutputParams src, OutputParams dst) {
        for (String key : src.keySet()) {
            if (!NO_COPY.contains(key)) {
                dst.setParam(src.get(key));
            }
        }
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        OutputParams groovyParams = this.createGroovyParams(params, "templates/groovy-fo/start_positions.gsp", this.getClass().getClassLoader());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        groovyParams.setParam(PARAM_OUTPUT_STREAM, os);
        output.write(groovyParams);
        byte[] gsp = os.toByteArray();
        transformer.write(stream, new ByteArrayInputStream(gsp));
    }
}
