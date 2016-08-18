package net.parostroj.timetable.output2;

import java.io.File;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.template.TemplateOutputResources;
import net.parostroj.timetable.output2.util.WrapperLogMap;

/**
 * Instance writes output of templates to disk.
 *
 * @author jub
 */
public class OutputWriter {

    public static final String OUTPUT_TEMPLATE_LOG_NAME = "net.parostroj.timetable.output2.Template";
    public static final String OUTPUT_SCRIPT_LOG_NAME = "net.parostroj.timetable.output2.Script";

    public interface ProcessListener {

        public void processed(net.parostroj.timetable.model.Output modelOutput);
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
            Locale returnedLocale = locale != null ? locale : Locale.getDefault();
            return LocalizedString.getOnlyLanguageLocale(returnedLocale);
        }

        public OutputParams createParams() {
            OutputParams params = new OutputParams();
            return params;
        }
    }

    private static final Logger scriptLog = LoggerFactory.getLogger(OUTPUT_SCRIPT_LOG_NAME);
    private static final Logger templateLog = LoggerFactory.getLogger(OUTPUT_TEMPLATE_LOG_NAME);

    private net.parostroj.timetable.model.Output errorOutput;

    private final TrainDiagram diagram;
    private final Settings settings;
    private final File outputDirectory;
    private final Iterable<net.parostroj.timetable.model.Output> modelOutputs;

    private ProcessListener listener;

    public OutputWriter(TrainDiagram diagram, Settings settings, File outputDirectory, Iterable<net.parostroj.timetable.model.Output> modelOutputs) {
        this.diagram = diagram;
        this.settings = settings;
        this.outputDirectory = outputDirectory;
        this.modelOutputs = modelOutputs;
    }

    public void setListener(ProcessListener listener) {
        this.listener = listener;
    }

    public void execute() throws OutputException {
        if (modelOutputs != null) {
            for (net.parostroj.timetable.model.Output modelOutput : modelOutputs) {
                try {
                    if (listener != null) {
                        listener.processed(modelOutput);
                    }
                    this.generateOutput(modelOutput);
                } catch (Exception e) {
                    errorOutput = modelOutput;
                    throw e;
                }
            }
        }
    }

    public net.parostroj.timetable.model.Output getErrorOutput() {
        return errorOutput;
    }

    private List<OutputSettings> createOutputs(net.parostroj.timetable.model.Output modelOutput) throws OutputException {
        OutputTemplate template = modelOutput.getTemplate();
        List<OutputSettings> result = null;
        if (template.getScript() != null) {
            final List<OutputSettings> out = new ArrayList<>();
            Map<String, Object> binding = new HashMap<>();
            binding.put("diagram", diagram);
            binding.put("output", modelOutput);
            binding.put("log", scriptLog);
            binding.put("settings", new WrapperLogMap<>(modelOutput.getSettings().getAttributesMap(OutputTemplate.CATEGORY_SETTINGS), scriptLog, "settings"));
            binding.put("localization", new WrapperLogMap<>(template.getAttributes().getAttributesMap(OutputTemplate.CATEGORY_I18N), scriptLog, "localization"));
            binding.put("locale", modelOutput.getLocale() != null ? modelOutput.getLocale() : settings.getLocale());
            binding.put("key", getOutputKey(modelOutput));
            binding.put("selection", modelOutput.getSelection());
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

    private String getOutputKey(net.parostroj.timetable.model.Output modelOutput) {
        return modelOutput.getKey() != null ? modelOutput.getKey() : modelOutput.getTemplate().getKey();
    }

    private void generateOutput(net.parostroj.timetable.model.Output modelOutput) throws OutputException {
        OutputTemplate template = modelOutput.getTemplate();
        String type = template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, String.class);
        OutputFactory factory = OutputFactory.newInstance(template.getOutput());
        factory.setParameter("locale", modelOutput.getLocale() != null? modelOutput.getLocale() : settings.getLocale());
        Output output = factory.createOutput(type);
        TextTemplate textTemplate = template.getTemplate();
        OutputResources resources = new TemplateOutputResources(template);
        List<OutputSettings> outputNames = this.createOutputs(modelOutput);
        if (outputNames == null) {
            this.generateOutput(
                    output,
                    this.getFile(null, getOutputKey(modelOutput),
                            template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class),
                            factory.getType()),
                    textTemplate, type, null,
                    this.updateContext(modelOutput, template, null), resources, null);
        } else {
            for(OutputSettings outputName : outputNames) {
                this.generateOutput(output, this.getFile(outputName.directory, outputName.name), textTemplate,
                        type, outputName.params,
                        this.updateContext(modelOutput, template, outputName.context), resources, outputName.encoding);
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

    private Map<String, Object> updateContext(net.parostroj.timetable.model.Output modelOutput,
            OutputTemplate outputTemplate, Map<String, Object> context) {
        if (context == null) context = new HashMap<>();
        context.put("settings", new WrapperLogMap<>(outputTemplate.getAttributes().getAttributesMap(OutputTemplate.CATEGORY_SETTINGS), templateLog, "settings"));
        context.put("localization", new WrapperLogMap<>(outputTemplate.getAttributes().getAttributesMap(OutputTemplate.CATEGORY_I18N), templateLog, "localization"));
        context.put("selection", modelOutput.getSelection());
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
