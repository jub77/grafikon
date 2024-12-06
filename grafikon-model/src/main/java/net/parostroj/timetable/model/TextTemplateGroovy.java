package net.parostroj.timetable.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Groovy text template.
 *
 * @author jub
 */
public final class TextTemplateGroovy implements TextTemplate {

    private static final Logger log = LoggerFactory.getLogger(TextTemplateGroovy.class);

    private static final Cache<String, Template> templateCache = CacheBuilder.newBuilder().softValues().build();

    private final String template;
    private Template templateGString;

    TextTemplateGroovy(String template) throws GrafikonException {
        this.template = template;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public Language getLanguage() {
        return Language.GROOVY;
    }

    private void initialize() throws GrafikonException {
        try {
            templateGString = templateCache.get(this.getTemplate(), () -> {
                TemplateEngine engine = new SimpleTemplateEngine();
                return engine.createTemplate(this.getTemplate());
            });
        } catch (Exception e) {
            throw new GrafikonException("Cannot create template: " + e.getMessage(), e.getCause(), GrafikonException.Type.TEXT_TEMPLATE);
        }
    }

    @Override
    public String evaluateWithException(Map<String, Object> binding) throws GrafikonException {
        try {
            if (templateGString == null) {
                initialize();
            }
            return templateGString.make(binding).toString();
        } catch (GrafikonException e) {
            throw e;
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
    public void evaluate(Writer output, Map<String, Object> binding) throws GrafikonException {
        if (templateGString == null) {
            initialize();
        }
        Writable result = templateGString.make(binding);
        try {
            result.writeTo(output);
            output.flush();
        } catch (IOException e) {
            throw new GrafikonException("Error writing output.", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TextTemplateGroovy that = (TextTemplateGroovy) o;
        return Objects.equals(template, that.template);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(template);
    }

    @Override
    public String toString() {
        return String.format("%s[%d]", getLanguage(), template != null ? template.length() : 0);
    }
}
