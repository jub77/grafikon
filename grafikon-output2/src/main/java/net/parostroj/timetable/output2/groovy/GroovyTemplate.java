package net.parostroj.timetable.output2.groovy;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.template.TemplateWriter;

import org.codehaus.groovy.control.CompilationFailedException;

public class GroovyTemplate {

    private final Template templateGString;
    private final GroovyTemplateBinding binding;

    public GroovyTemplate(Reader reader, GroovyTemplateBinding binding) throws OutputException {
        this.binding = binding;
        if (reader != null) {
            TemplateEngine engine = new SimpleTemplateEngine();
            try {
                this.templateGString = engine.createTemplate(reader);
            } catch (CompilationFailedException | ClassNotFoundException | IOException e) {
                throw new OutputException("Error loading template", e);
            }
        } else {
            this.templateGString = null;
        }
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
            } else if (templateGString != null) {
                templateGString.make(map).writeTo(new OutputStreamWriter(output, outputEncoding));
            } else {
                throw new OutputException("No default template");
            }
            binding.postProcess(diagram, params, map);
        } catch (UnsupportedEncodingException e) {
            throw new OutputException("Error creating writer", e);
        } catch (OutputException e) {
            throw e;
        } catch (Exception e) {
            throw new OutputException("Error evaluating template", e);
        }
    }

    public TemplateWriter get(Charset outputEncoding) {
        return (output, params, diagram, locale) -> write(output, params, diagram, locale, outputEncoding);
    }
}
