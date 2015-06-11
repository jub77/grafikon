package net.parostroj.timetable.output2.groovy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.template.TemplateWriter;
import net.parostroj.timetable.output2.util.ResourceHelper;

public class GroovyTemplateFactory {

    private static final Map<String, String> TEMPLATES;
    private static final Map<String, GroovyTemplateBinding> BINDINGS;

    static {
        // templates
        Map<String, String> templates = new HashMap<>();
        templates.put("starts", "start_positions.gsp");
        templates.put("ends", "end_positions.gsp");
        templates.put("trains", "trains.gsp");
        templates.put("stations", "stations.gsp");
        templates.put("train_unit_cycles", "train_unit_cycles.gsp");
        templates.put("driver_cycles", "driver_cycles.gsp");
        templates.put("engine_cycles", "engine_cycles.gsp");
        templates.put("custom_cycles", "custom_cycles.gsp");
        TEMPLATES = Collections.unmodifiableMap(templates);

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

    private final Map<String, GroovyTemplate> cachedTemplates;
    private final String templateBaseLocation;

    public GroovyTemplateFactory(String templateBaseLocation) {
        this.templateBaseLocation = templateBaseLocation;
        this.cachedTemplates = new HashMap<>();
    }

    public TemplateWriter getTemplate(String type, Charset outputEncoding) throws OutputException {
        GroovyTemplate template = cachedTemplates.get(type);
        if (template == null) {
            String templateLocation = templateBaseLocation + TEMPLATES.get(type);
            InputStream is = ResourceHelper.getStream(templateLocation, this.getClass().getClassLoader());
            template = new GroovyTemplate(is != null ? new InputStreamReader(is, StandardCharsets.UTF_8) : null, BINDINGS.get(type));
            cachedTemplates.put(type, template);
        }
        return template.get(outputEncoding);
    }
}
