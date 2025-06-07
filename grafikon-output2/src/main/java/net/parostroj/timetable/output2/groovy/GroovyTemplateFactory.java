package net.parostroj.timetable.output2.groovy;

import java.nio.charset.Charset;
import java.util.Map;

import net.parostroj.timetable.output2.template.TemplateWriter;

public class GroovyTemplateFactory {

    private static final Map<String, GroovyTemplateBinding> BINDINGS = Map.of(
            "starts", new StartsTemplateBinding(),
            "ends", new EndsTemplateBinding(),
            "trains", new TrainsTemplateBinding(),
            "diagram", new DiagramTemplateBinding(),
            "stations", new StationsTemplateBinding(),
            "train_unit_cycles", new TrainUnitCyclesTemplateBinding(),
            "driver_cycles", new DriverCyclesTemplateBinding(),
            "engine_cycles", new EngineCyclesTemplateBinding(),
            "custom_cycles", new CustomCyclesTemplateBinding());

    public TemplateWriter getTemplate(String type, Charset outputEncoding) {
        return new GroovyTemplate(BINDINGS.get(type)).get(outputEncoding);
    }
}
