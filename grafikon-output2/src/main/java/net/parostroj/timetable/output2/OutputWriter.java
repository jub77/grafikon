package net.parostroj.timetable.output2;

import java.io.File;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.template.TemplateOutputResources;

/**
 * Instance writes output of templates to disk.
 *
 * @author jub
 */
public class OutputWriter {

    public interface ProcessListener {

        public void processed(OutputTemplate template);
    }

    public interface OutputCollector {

        public void add(String name, Map<String, Object> context);

        public void add(String name, Map<String, Object> context, String encoding);

        public OutputSettings create();
    }

    public static class OutputSettings {

        String name;
        String encoding;
        String directory;
        Map<String, Object> context;
        Map<String, Object> params;

        public OutputSettings setName(String name) {
            this.name = name;
            return this;
        }

        public OutputSettings setContext(Map<String, Object> context) {
            this.context = context;
            return this;
        }

        public OutputSettings setEncoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public OutputSettings setParams(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public OutputSettings setDirectory(String directory) {
            this.directory = directory;
            return this;
        }
    }

    public static class Settings {

        private final Locale locale;

        public Settings() {
            this(Locale.getDefault());
        }

        public Settings(Locale locale) {
            this.locale = locale;
        }

        public Locale getLocale() {
            return locale;
        }

        public OutputParams createParams() {
            OutputParams params = new OutputParams();
            return params;
        }
    }

    private static final Logger scriptLog = LoggerFactory.getLogger("net.parostroj.timetable.output2.Script");

    private OutputTemplate errorTemplate;

    private final TrainDiagram diagram;
    private final Settings settings;
    private final File outputDirectory;
    private final Iterable<OutputTemplate> templates;

    private ProcessListener listener;

    public OutputWriter(TrainDiagram diagram, Settings settings, File outputDirectory, Iterable<OutputTemplate> templates) {
        this.diagram = diagram;
        this.settings = settings;
        this.outputDirectory = outputDirectory;
        this.templates = templates;
    }

    public void setListener(ProcessListener listener) {
        this.listener = listener;
    }

    public void execute() throws OutputException {
        if (templates != null) {
            for (OutputTemplate template : templates) {
                try {
                    if (listener != null) {
                        listener.processed(template);
                    }
                    this.generateOutput(template);
                } catch (Exception e) {
                    errorTemplate = template;
                    throw e;
                }
            }
        }
    }

    public OutputTemplate getErrorTemplate() {
        return errorTemplate;
    }

    private List<OutputSettings> createOutputs(OutputTemplate template) throws OutputException {
        List<OutputSettings> result = null;
        if (template.getScript() != null) {
            final List<OutputSettings> out = new ArrayList<OutputSettings>();
            Map<String, Object> binding = new HashMap<String, Object>();
            binding.put("diagram", diagram);
            binding.put("template", template);
            binding.put("log", scriptLog);
            binding.put("settings", template.getAttributes().getAttributesMap(OutputTemplate.CATEGORY_SETTINGS));
            binding.put("localization", template.getAttributes().getAttributesMap(OutputTemplate.CATEGORY_I18N));
            binding.put("outputs", new OutputCollector() {
                @Override
                public void add(String name, Map<String, Object> context) {
                    out.add(new OutputSettings().setName(name).setContext(context));
                }

                @Override
                public void add(String name, Map<String, Object> context, String encoding) {
                    out.add(new OutputSettings().setName(name).setContext(context).setEncoding(encoding));
                }

                @Override
                public OutputSettings create() {
                    OutputSettings settings = new OutputSettings();
                    out.add(settings);
                    return settings;
                }
            });
            try {
                template.getScript().evaluateWithException(binding);
            } catch (GrafikonException e) {
                throw new OutputException("Error in script: " + e.getMessage(), e);
            }
            result = out;
        }
        return result;
    }

    private void generateOutput(OutputTemplate template) throws OutputException {
        String type = template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, String.class);
        OutputFactory factory = OutputFactory.newInstance(template.getOutput());
        factory.setParameter("locale", settings.getLocale());
        Output output = factory.createOutput(type);
        TextTemplate textTemplate = template.getTemplate();
        OutputResources resources = new TemplateOutputResources(template);
        List<OutputSettings> outputNames = this.createOutputs(template);
        if (outputNames == null) {
            this.generateOutput(
                    output,
                    this.getFile(null, template.getName(),
                            template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class),
                            factory.getType()),
                    textTemplate, type, null,
                    this.updateContext(template, new HashMap<>()), resources, null);
        } else {
            for(OutputSettings outputName : outputNames) {
                this.generateOutput(output, this.getFile(outputName.directory, outputName.name), textTemplate,
                        type, outputName.params,
                        this.updateContext(template, outputName.context), resources, outputName.encoding);
            }
        }
    }

    private void generateOutput(Output output, File outpuFile, TextTemplate textTemplate, String type,
            Map<String, Object> parameters, Map<String, Object> context, OutputResources resources, String encoding) throws OutputException {
        OutputParams params = settings.createParams();

        if (textTemplate != null) {
            params.setParam(Output.PARAM_TEXT_TEMPLATE, textTemplate);
        }
        if (resources != null) {
            params.setParam(Output.PARAM_RESOURCES, resources);
        }
        params.setParam(Output.PARAM_TRAIN_DIAGRAM, diagram);
        params.setParam(Output.PARAM_OUTPUT_FILE, outpuFile);
        params.setParam(Output.PARAM_CONTEXT, context);
        if (encoding != null) {
            params.setParam(Output.PARAM_OUTPUT_ENCODING, encoding);
        }
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                params.setParam(key, parameters.get(key));
            }
        }
        output.write(params);
    }

    private Map<String, Object> updateContext(OutputTemplate outputTemplate, Map<String, Object> context) {
        context.put("settings", outputTemplate.getAttributes().getAttributesMap(OutputTemplate.CATEGORY_SETTINGS));
        context.put("localization", outputTemplate.getAttributes().getAttributesMap(OutputTemplate.CATEGORY_I18N));
        return context;
    }

    private File getFile(String directory, String name) {
        name = name.replaceAll("[\\\\:/\"?<>|*]", "");
        File dir = getDir(directory);
        return new File(dir, name);
    }

    private File getFile(String directory, String name, String extension, String type) {
        name = name.replaceAll("[\\\\:/\"?<>|*]", "");
        File dir = getDir(directory);
        return new File(dir, name + "." + (extension == null ? getDefaultExtension(type) : extension));
    }

    private String getDefaultExtension(String type) {
        String extension = null;
        switch (type) {
            case "draw": extension = "svg"; break;
            case "groovy": extension = "html"; break;
            case "pdf.groovy": extension = "pdf"; break;
            case "xml": extension = "xml"; break;
            default: extension = "unknown"; break;
        }
        return extension;
    }

    private File getDir(String directory) {
        File dir = (directory == null) ? outputDirectory : new File(outputDirectory, directory);
        dir.mkdirs();
        return dir;
    }
}
