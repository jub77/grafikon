package net.parostroj.timetable.output2.template;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import net.parostroj.timetable.model.ExecutableTextTemplate;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;

/**
 * Gsp output.
 *
 * @author jub
 */
public abstract class TemplateOutput extends OutputWithLocale {

    public static final String TRANSLATOR = "translator";

    private final Supplier<ExecutableTextTemplate> templateSupplier;

    public TemplateOutput(Locale locale, Supplier<ExecutableTextTemplate> defaultTemplateSupplier) {
        super(locale);
        this.templateSupplier = defaultTemplateSupplier;
    }

    protected ExecutableTextTemplate processParams(OutputParams params) {
        ExecutableTextTemplate template = params.getParamValue(PARAM_TEMPLATE, ExecutableTextTemplate.class);
        if (template == null) {
            template = templateSupplier.get();
        }
        return template;
    }

    @Override
    protected void writeTo(OutputParams params, OutputStream stream, TrainDiagram diagram) throws OutputException {
        ExecutableTextTemplate template = processParams(params);
        // TODO implementation
        Map<String, Object> binding = new HashMap<>();
        this.addContext(params, binding);
        this.writeOutput(template, stream, binding);
    }

    protected void writeOutput(ExecutableTextTemplate template, OutputStream stream, Map<String, Object> binding) throws OutputException {
        try {
            template.evaluate(stream, binding, "utf-8");
        } catch (GrafikonException e) {
            throw new OutputException("Error writing output", e);
        }
    }

    protected void addContext(OutputParams params, Map<String, Object> map) {
        map.put("diagram", params.getParam(PARAM_TRAIN_DIAGRAM).getValue());
        map.put("locale", this.leaveOnlyLanguage(this.getLocale()));
        if (params.paramExistWithValue(PARAM_CONTEXT)) {
            Map<?, ?> context = params.get(PARAM_CONTEXT).getValue(Map.class);
            for (Map.Entry<?, ?> entry : context.entrySet()) {
                map.put((String) entry.getKey(), entry.getValue());
            }
        }
    }

    private Locale leaveOnlyLanguage(Locale locale) {
        return Locale.forLanguageTag(locale.getLanguage());
    }
}
