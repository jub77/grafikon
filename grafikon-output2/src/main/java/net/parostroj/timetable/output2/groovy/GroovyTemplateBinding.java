package net.parostroj.timetable.output2.groovy;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;

public abstract class GroovyTemplateBinding {

    public static final String LOCALIZATION_BUNDLE = "texts/html_texts";
    public static final String TRANSLATOR = "translator";
    public static final String LOCALIZATION = "localization";

    private static final Logger templateLog = LoggerFactory.getLogger("net.parostroj.timetable.output2.Template");

    protected void addContext(OutputParams params, Map<String, Object> binding) {
        binding.put("diagram", params.getParam(Output.PARAM_TRAIN_DIAGRAM).getValue());
        if (params.paramExistWithValue(Output.PARAM_CONTEXT)) {
            Map<?, ?> context = params.get(Output.PARAM_CONTEXT).getValue(Map.class);
            for (Map.Entry<?, ?> entry : context.entrySet()) {
                binding.put((String) entry.getKey(), entry.getValue());
            }
        }
        if (params.paramExistWithValue(Output.PARAM_SETTINGS)) {
            binding.put("settings", params.getParamValue(Output.PARAM_SETTINGS, Object.class));
        } else {
            binding.put("settings", Collections.emptyMap());
        }
    }

    public Map<String, Object> get(TrainDiagram diagram, OutputParams params, Locale locale) {
        Map<String, Object> binding = new HashMap<>();
        this.addSpecific(params, binding, diagram, locale);
        binding.put("images", new HashSet<String>());
        binding.put("log", templateLog);
        this.addContext(params, binding);
        this.addLocale(locale, binding);
        return binding;
    }

    protected void addLocale(Locale locale, Map<String, Object> map) {
        map.put("locale", this.leaveOnlyLanguage(locale));
    }

    private Locale leaveOnlyLanguage(Locale locale) {
        return Locale.forLanguageTag(locale.getLanguage());
    }

    public void postProcess(TrainDiagram diagram, OutputParams params, Map<String, Object> binding) throws OutputException {
        // write images if possible
        Set<?> images = (Set<?>) binding.get("images");
        if (images != null && params.paramExist(Output.PARAM_OUTPUT_FILE)) {
            File file = params.getParamValue(Output.PARAM_OUTPUT_FILE, File.class);
            file = file.getParentFile();
            // for all images ...
            ImageSaver saver = new ImageSaver(diagram);
            for (Object image : images) {
                try {
                    saver.saveImage((String) image, file,
                            params.getParamValue(Output.PARAM_RESOURCES, OutputResources.class));
                } catch (IOException e) {
                    throw new OutputException("Error saving image: " + image, e);
                }
            }
        }
    }

    abstract protected void addSpecific(OutputParams params, Map<String, Object> binding, TrainDiagram diagram, Locale locale);
}
