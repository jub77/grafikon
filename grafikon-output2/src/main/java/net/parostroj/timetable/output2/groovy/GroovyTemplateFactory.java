package net.parostroj.timetable.output2.groovy;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.template.TemplateWriter;

public class GroovyTemplateFactory {

    private static final Map<String, GroovyTemplateBinding> BINDINGS;

    static {
        // binding
        Map<String, GroovyTemplateBinding> bindingCreators = new HashMap<>();
        bindingCreators.put("starts", new StartsTemplateBinding());
        bindingCreators.put("ends", new EndsTemplateBinding());
        bindingCreators.put("trains", new TrainsTemplateBinding());
        bindingCreators.put("diagram", new DiagramTemplateBinding());
        bindingCreators.put("stations", new StationsTemplateBinding());
        bindingCreators.put("train_unit_cycles", new TrainUnitCyclesTemplateBinding());
        bindingCreators.put("driver_cycles", new DriverCyclesTemplateBinding());
        bindingCreators.put("engine_cycles", new EngineCyclesTemplateBinding());
        bindingCreators.put("custom_cycles", new CustomCyclesTemplateBinding());
        BINDINGS = Collections.unmodifiableMap(bindingCreators);
    }

    public GroovyTemplateFactory() {
    }

    public TemplateWriter getTemplate(String type, Charset outputEncoding) throws OutputException {
        return new GroovyTemplate(BINDINGS.get(type)).get(outputEncoding);
    }
}
