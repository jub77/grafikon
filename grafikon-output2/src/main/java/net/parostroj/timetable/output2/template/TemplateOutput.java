package net.parostroj.timetable.output2.template;

import java.io.*;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;

/**
 * Gsp output.
 *
 * @author jub
 */
public class TemplateOutput extends OutputWithLocale {

    private final TemplateWriterFactory templateWriterFactory;
    private final TemplateBindingHandler bindingHandler;

    public TemplateOutput(Locale locale, TemplateWriterFactory defaultTemplateFactory,
            TemplateBindingHandler bindingHandler) {
        super(locale);
        this.templateWriterFactory = defaultTemplateFactory;
        this.bindingHandler = bindingHandler;
    }

    protected TemplateWriter processParams(OutputParams params) throws OutputException {
        TemplateWriter template = params.getParamValue(PARAM_TEMPLATE, TemplateWriter.class);
        if (template == null) {
            template = templateWriterFactory.get();
        }
        return template;
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        TemplateWriter template = processParams(params);
        Map<String, Object> binding = bindingHandler.get(diagram, params, this.getLocale());
        this.addLocale(params, binding);
        this.writeOutput(template, stream, binding);
        bindingHandler.postProcess(diagram, params, binding);
    }

    protected void writeOutput(TemplateWriter template, OutputStream stream, Map<String, Object> binding) throws OutputException {
        template.write(stream, binding);
    }

    protected void addLocale(OutputParams params, Map<String, Object> map) {
        map.put("locale", this.leaveOnlyLanguage(this.getLocale()));
    }

    private Locale leaveOnlyLanguage(Locale locale) {
        return Locale.forLanguageTag(locale.getLanguage());
    }
}
