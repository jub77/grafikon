package net.parostroj.timetable.output2.groovy;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.*;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.ExecutableTextTemplate;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.output2.OutputException;

public class GroovyExecutableTextTemplate implements ExecutableTextTemplate {

    private static final Logger log = LoggerFactory.getLogger(GroovyExecutableTextTemplate.class);

    private final Template templateGString;

    public GroovyExecutableTextTemplate(Reader reader) throws OutputException {
        TemplateEngine engine = new SimpleTemplateEngine();
        try {
            templateGString = engine.createTemplate(reader);
        } catch (CompilationFailedException | ClassNotFoundException | IOException e) {
            throw new OutputException("Error loading template", e);
        }
    }


    @Override
    public String evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            return templateGString.make(binding).toString();
        } catch (Exception e) {
            throw new GrafikonException("Error evaluating template: " + e.getMessage(), e, GrafikonException.Type.TEXT_TEMPLATE);
        }
    }

    @Override
    public String evaluate(Map<String, Object> binding) {
        try {
            return this.evaluateWithException(binding);
        } catch (GrafikonException e) {
            log.warn(e.getMessage(), e);
            return "-- Template error --";
        }
    }

    @Override
    public void evaluate(OutputStream output, Map<String, Object> binding, String encoding) throws GrafikonException {
        try {
            this.evaluate(new OutputStreamWriter(output, encoding), binding);
        } catch (UnsupportedEncodingException e) {
            throw new GrafikonException("Error creating writer.", e);
        }
    }

    @Override
    public void evaluate(Writer output, Map<String, Object> binding) throws GrafikonException {
        Writable result = templateGString.make(binding);
        try {
            result.writeTo(output);
            output.flush();
        } catch (IOException e) {
            throw new GrafikonException("Error writing output.", e);
        }
    }
}
