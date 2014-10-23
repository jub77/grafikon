package net.parostroj.timetable.output2;

import java.io.File;
import java.util.*;

import net.parostroj.timetable.model.*;

public class OutputAction {

    public interface OutputCollector {

        public void add(String name, Map<String, Object> context);

        public void add(String name, Map<String, Object> context, Map<String, Object> params);

        public void add(String name, Map<String, Object> context, String encoding);

        public void add(String name, Map<String, Object> context, Map<String,Object> params, String encoding);
    }

    private static class OutputSetting {

        public String name;
        public Map<String, Object> binding;
        public String encoding;
        public Map<String, Object> params;

        public OutputSetting(String name, Map<String, Object> binding, String encoding, Map<String, Object> params) {
            this.name = name;
            this.binding = binding;
            this.encoding = encoding;
            this.params = params;
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

    public OutputAction(TrainDiagram diagram, Settings settings, File outputDirectory, Iterable<OutputTemplate> templates) {
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

    private List<OutputSetting> createOutputs(OutputTemplate template) {
        List<OutputSetting> result = null;
        if (template.getScript() != null) {
            final List<OutputSetting> out = new ArrayList<OutputSetting>();
            Map<String, Object> binding = new HashMap<String, Object>();
            binding.put("diagram", diagram);
            binding.put("outputs", new OutputCollector() {
                @Override
                public void add(String name, Map<String, Object> context) {
                    out.add(new OutputSetting(name, context, null, null));
                }

                @Override
                public void add(String name, Map<String, Object> context, Map<String, Object> params) {
                    out.add(new OutputSetting(name, context, null, params));
                }

                @Override
                public void add(String name, Map<String, Object> context, String encoding) {
                    out.add(new OutputSetting(name, context, encoding, null));
                }

                @Override
                public void add(String name, Map<String, Object> context, Map<String, Object> params, String encoding) {
                    out.add(new OutputSetting(name, context, encoding, params));
                }
            });
            template.getScript().evaluate(binding);
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
        List<OutputSetting> outputNames = this.createOutputs(template);
        if (outputNames == null) {
            this.generateOutput(
                    output,
                    this.getFile(template.getName(),
                            template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class)),
                    textTemplate, type, null, null, null);
            if ("trains".equals(type)) {
                // for each driver circulation
                Map<String, Object> parameters = new HashMap<String, Object>();
                for (TrainsCycle cycle : diagram.getCycles(diagram.getEngineCycleType())) {
                    parameters.put("driver_cycle", cycle);
                    this.generateOutput(
                            output,
                            this.getFile(template.getName() + "_" + cycle.getName(),
                                    template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class)),
                            textTemplate, type, parameters, null, null);
                }
            }
        } else {
            for(OutputSetting outputName : outputNames) {
                this.generateOutput(output, this.getFile(outputName.name), textTemplate, type, outputName.params, outputName.binding, outputName.encoding);
            }
        }
    }

    private void generateOutput(Output output, File outpuFile, TextTemplate textTemplate, String type,
            Map<String, Object> parameters, Map<String, Object> context, String encoding) throws OutputException {
        OutputParams params = settings.createParams();
        if (textTemplate != null) {
            params.setParam(DefaultOutputParam.TEXT_TEMPLATE, textTemplate);
        }
        params.setParam(DefaultOutputParam.TRAIN_DIAGRAM, diagram);
        params.setParam(DefaultOutputParam.OUTPUT_FILE, outpuFile);
        if (context != null) {
            params.setParam(DefaultOutputParam.CONTEXT, context);
        }
        if (encoding != null) {
            params.setParam(DefaultOutputParam.OUTPUT_ENCODING, encoding);
        }
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                params.setParam(key, parameters.get(key));
            }
        }
        output.write(params);
    }

    private File getFile(String name) {
        name = name.replaceAll("[\\\\:/\"?<>|]", "");
        return new File(outputDirectory, name);
    }

    private File getFile(String name, String extension) {
        name = name.replaceAll("[\\\\:/\"?<>|]", "");
        return new File(outputDirectory, name + "." + (extension == null ? "html" : extension));
    }
}
