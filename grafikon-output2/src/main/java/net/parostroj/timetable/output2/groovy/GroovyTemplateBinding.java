package net.parostroj.timetable.output2.groovy;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputParams;
import net.parostroj.timetable.output2.template.TemplateBindingHandler;

public abstract class GroovyTemplateBinding implements TemplateBindingHandler {

    public static final String LOCALIZATION_BUNDLE = "texts/html_texts";
    public static final String TRANSLATOR = "translator";

    protected void addContext(OutputParams params, Map<String, Object> binding) {
        binding.put("diagram", params.getParam(Output.PARAM_TRAIN_DIAGRAM).getValue());
        if (params.paramExistWithValue(Output.PARAM_CONTEXT)) {
            Map<?, ?> context = params.get(Output.PARAM_CONTEXT).getValue(Map.class);
            for (Map.Entry<?, ?> entry : context.entrySet()) {
                binding.put((String) entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Map<String, Object> get(TrainDiagram diagram, OutputParams params, Locale locale) {
        Map<String, Object> binding = new HashMap<>();
        this.addSpecific(params, binding, diagram, locale);
        this.addContext(params, binding);
        return binding;
    }

    @Override
    public void postProcess(TrainDiagram diagram, OutputParams params, Map<String, Object> binding) throws OutputException {
        // default implementation do nothing
    }

    abstract protected void addSpecific(OutputParams params, Map<String, Object> binding, TrainDiagram diagram, Locale locale);
}
