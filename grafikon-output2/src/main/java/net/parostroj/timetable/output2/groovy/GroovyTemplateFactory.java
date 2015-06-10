package net.parostroj.timetable.output2.groovy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.template.TemplateBindingHandler;
import net.parostroj.timetable.output2.template.TemplateWriter;
import net.parostroj.timetable.output2.util.ResourceHelper;

public class GroovyTemplateFactory {

    private static final String TEMPLATES_BASE_LOCATION = "templates/groovy/";
    private static final Map<String, String> TEMPLATES;
    private static final Map<String, TemplateBindingHandler> BINDING_CREATORS;

    static {
        // templates
        Map<String, String> templates = new HashMap<>();
        templates.put("starts", "start_positions.gsp");
        templates.put("trains", "trains.gsp");
        TEMPLATES = Collections.unmodifiableMap(templates);

        // binding
        Map<String, TemplateBindingHandler> bindingCreators = new HashMap<>();
        bindingCreators.put("starts", new StartsTemplateBinding());
        bindingCreators.put("trains", new TrainsTemplateBinding());
        bindingCreators.put("diagram", new DiagramTemplateBinding());
        BINDING_CREATORS = Collections.unmodifiableMap(bindingCreators);
    }

    private final Map<String, GroovyTemplate> cachedTemplates;

    public GroovyTemplateFactory() {
        this.cachedTemplates = new HashMap<>();
    }

    public TemplateWriter getTemplate(String type, Charset outputEncoding) throws OutputException {
        GroovyTemplate template = cachedTemplates.get(type);
        if (template == null) {
            String templateLocation = TEMPLATES_BASE_LOCATION + TEMPLATES.get(type);
            InputStream is = ResourceHelper.getStream(templateLocation, this.getClass().getClassLoader());
            if (is == null) {
                throw new OutputException("No default template found: " + templateLocation);
            }
            template = new GroovyTemplate(new InputStreamReader(is, StandardCharsets.UTF_8));
            cachedTemplates.put(type, template);
        }
        return template.get(outputEncoding);
    }

    public TemplateBindingHandler getBinding(String type) {
        return BINDING_CREATORS.get(type);
    }
}
