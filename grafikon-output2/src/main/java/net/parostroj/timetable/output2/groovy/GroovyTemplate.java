package net.parostroj.timetable.output2.groovy;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.template.TemplateWriter;

public class GroovyTemplate {

    private final GroovyTemplateBinding binding;

    public GroovyTemplate(GroovyTemplateBinding binding) throws OutputException {
        this.binding = binding;
    }

    private TextTemplate getTextTemplate(OutputParams params) {
        return params.getParamValue(Output.PARAM_TEXT_TEMPLATE, TextTemplate.class);
    }

    private void write(OutputStream output, OutputParams params, TrainDiagram diagram, Locale locale, Charset outputEncoding) throws OutputException {
        try {
            Map<String, Object> map = binding.get(diagram, params, locale);
            TextTemplate textTemplate = getTextTemplate(params);
            if (textTemplate != null) {
                textTemplate.evaluate(output, map, outputEncoding.name());
            } else {
                throw new OutputException("Template missing");
            }
            binding.postProcess(diagram, params, map);
        } catch (OutputException e) {
            throw e;
        } catch (Exception e) {
            throw new OutputException("Error evaluating template", e);
        }
    }

    public TemplateWriter get(Charset outputEncoding) {
        return (output, params, locale) -> {
            write(output, params, params.getParamValue(Output.PARAM_TRAIN_DIAGRAM, TrainDiagram.class), locale,
                    outputEncoding);
        };
    }
}
