package net.parostroj.timetable.output2.groovy;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.utils.ObjectsUtil;

public abstract class GroovyTemplateBinding {

    public static final String CONTEXT_SETTINGS = "settings";
    public static final String CONTEXT_LOCALIZATION = "localization";

    private static final Logger templateLog = LoggerFactory.getLogger("net.parostroj.timetable.output2.Template");

    public Map<String, Object> get(TrainDiagram diagram, OutputParams params, Locale locale) {
        Map<String, Object> binding = new HashMap<>();
        this.addSpecific(params, binding, diagram, locale);
        binding.put("diagram", diagram);
        binding.put("images", new HashSet<String>());
        binding.put("log", templateLog);
        this.addContext(params, binding);
        this.addLocale(locale, binding);
        return binding;
    }

    protected void addContext(OutputParams params, Map<String, Object> binding) {
        if (params.paramExistWithValue(Output.PARAM_CONTEXT)) {
            Map<?, ?> context = params.get(Output.PARAM_CONTEXT).getValue(Map.class);
            for (Map.Entry<?, ?> entry : context.entrySet()) {
                binding.put((String) entry.getKey(), entry.getValue());
            }
        }
        // if context didn't contain settings - create empty map
        if (!binding.containsKey(CONTEXT_SETTINGS)) {
            binding.put(CONTEXT_SETTINGS, Collections.emptyMap());
        }
    }

    protected void addLocale(Locale locale, Map<String, Object> map) {
        map.put("locale", this.leaveOnlyLanguage(locale));
    }

    private Locale leaveOnlyLanguage(Locale locale) {
        return Locale.forLanguageTag(locale.getLanguage());
    }

    public void postProcess(TrainDiagram diagram, OutputParams params, Map<String, Object> binding) throws OutputException {
        // write images if possible
        Collection<String> images = ObjectsUtil.checkedCollection((Collection<?>) binding.get("images"), String.class);
        if (images != null && params.paramExist(Output.PARAM_OUTPUT_FILE)) {
            File file = params.getParamValue(Output.PARAM_OUTPUT_FILE, File.class);
            file = file.getParentFile();
            // for all images ...
            ImageSaver saver = new ImageSaver(diagram);
            OutputResources resources = params.getParamValue(Output.PARAM_RESOURCES, OutputResources.class);
            for (String image : images) {
                try {
                    saver.saveImage(image, file, resources);
                } catch (IOException e) {
                    throw new OutputException("Error saving image: " + image, e);
                }
            }
        }
    }

    abstract protected void addSpecific(OutputParams params, Map<String, Object> binding, TrainDiagram diagram, Locale locale);
}
