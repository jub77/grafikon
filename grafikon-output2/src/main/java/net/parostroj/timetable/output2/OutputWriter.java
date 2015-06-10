package net.parostroj.timetable.output2;

import java.io.File;
import java.util.*;

import net.parostroj.timetable.model.*;

/**
 * Instance writes output of templates to disk.
 *
 * @author jub
 */
public class OutputWriter {

    public interface OutputCollector {

        public void add(String name, Map<String, Object> context);

        public void add(String name, Map<String, Object> context, String encoding);

        public OutputSettings create();
    }

    public static class OutputSettings {

        public String name;
        public Map<String, Object> context;
        public String encoding;
        public Map<String, Object> params;
        public String directory;

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

        private final boolean title;
        private final boolean twoSided;
        private final boolean techTimes;
        private final Locale locale;

        public Settings() {
            this(Locale.getDefault());
        }

        public Settings(Locale locale) {
            this(true, true, false, locale);
        }

        public Settings(boolean title, boolean twoSided, boolean techTimes, Locale locale) {
            this.title = title;
            this.twoSided = twoSided;
            this.techTimes = techTimes;
            this.locale = locale;
        }

        public boolean isTitle() {
            return title;
        }

        public boolean isTwoSided() {
            return twoSided;
        }

        public boolean isTechTimes() {
            return techTimes;
        }

        public Locale getLocale() {
            return locale;
        }

        public OutputParams createParams() {
            OutputParams params = new OutputParams();
            if (title) {
                params.setParam("title.page", true);
            }
            params.setParam("page.sort", twoSided ? "two_sides" : "one_side");
            if (techTimes) {
                params.setParam("tech.time", true);
            }
            return params;
        }
    }

    private OutputTemplate errorTemplate;

    private final TrainDiagram diagram;
    private final Settings settings;
    private final File outputDirectory;
    private final Iterable<OutputTemplate> templates;

    public OutputWriter(TrainDiagram diagram, Settings settings, File outputDirectory, Iterable<OutputTemplate> templates) {
        this.diagram = diagram;
        this.settings = settings;
        this.outputDirectory = outputDirectory;
        this.templates = templates;
    }

    public void execute() throws OutputException {
        if (templates != null) {
            for (OutputTemplate template : templates) {
                try {
                    this.generateOutput(template);
                } catch (OutputException e) {
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
        TextTemplate textTemplate = template.getAttributes().getBool(OutputTemplate.ATTR_DEFAULT_TEMPLATE) ? null : template.getTemplate();
        List<OutputSettings> outputNames = this.createOutputs(template);
        if (outputNames == null) {
            this.generateOutput(
                    output,
                    this.getFile(null, template.getName(),
                            template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class)),
                    textTemplate, type, null, null, null);
        } else {
            for(OutputSettings outputName : outputNames) {
                this.generateOutput(output, this.getFile(outputName.directory, outputName.name), textTemplate, type, outputName.params, outputName.context, outputName.encoding);
            }
        }
    }

    private void generateOutput(Output output, File outpuFile, TextTemplate textTemplate, String type,
            Map<String, Object> parameters, Map<String, Object> context, String encoding) throws OutputException {
        OutputParams params = settings.createParams();
        if (textTemplate != null) {
            params.setParam(Output.PARAM_TEMPLATE, textTemplate);
        }
        params.setParam(Output.PARAM_TRAIN_DIAGRAM, diagram);
        params.setParam(Output.PARAM_OUTPUT_FILE, outpuFile);
        if (context != null) {
            params.setParam(Output.PARAM_CONTEXT, context);
        }
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

    private File getFile(String directory, String name) {
        name = name.replaceAll("[\\\\:/\"?<>|*]", "");
        File dir = getDir(directory);
        return new File(dir, name);
    }

    private File getFile(String directory, String name, String extension) {
        name = name.replaceAll("[\\\\:/\"?<>|*]", "");
        File dir = getDir(directory);
        return new File(dir, name + "." + (extension == null ? "html" : extension));
    }

    private File getDir(String directory) {
        File dir = (directory == null) ? outputDirectory : new File(outputDirectory, directory);
        dir.mkdirs();
        return dir;
    }
}
