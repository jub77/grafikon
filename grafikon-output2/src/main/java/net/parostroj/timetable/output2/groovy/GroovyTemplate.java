package net.parostroj.timetable.output2.groovy;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.template.TemplateWriter;

import org.codehaus.groovy.control.CompilationFailedException;

public class GroovyTemplate {

    private final Template templateGString;

    public GroovyTemplate(Reader reader) throws OutputException {
        TemplateEngine engine = new SimpleTemplateEngine();
        try {
            this.templateGString = engine.createTemplate(reader);
        } catch (CompilationFailedException | ClassNotFoundException | IOException e) {
            throw new OutputException("Error loading template", e);
        }
    }

    private void write(OutputStream output, Map<String, Object> binding, Charset outputEncoding) throws OutputException {
        try {
            templateGString.make(binding).writeTo(new OutputStreamWriter(output, outputEncoding));
        } catch (UnsupportedEncodingException e) {
            throw new OutputException("Error creating writer", e);
        } catch (Exception e) {
            throw new OutputException("Error evaluating template", e);
        }
    }

    public TemplateWriter get(Charset outputEncoding) {
        return (output, binding) -> write(output, binding, outputEncoding);
    }
}
