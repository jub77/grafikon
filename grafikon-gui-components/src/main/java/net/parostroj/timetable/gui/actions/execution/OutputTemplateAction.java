package net.parostroj.timetable.gui.actions.execution;

import java.io.File;
import java.util.*;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputTemplateAction extends EventDispatchAfterModelAction {

    private interface OutputCollector {

        public void add(String name, Map<String, Object> values);

        public void add(String name, Map<String, Object> values, String encoding);
    }

    private static class OutputSetting {

        public String name;
        public Map<String, Object> binding;
        public String encoding;

        public OutputSetting(String name, Map<String, Object> binding, String encoding) {
            this.name = name;
            this.binding = binding;
            this.encoding = encoding;
        }
    }

    public static class Settings {

        private final boolean title;
        private final boolean twoSided;
        private final boolean techTimes;

        public Settings(boolean title, boolean twoSided, boolean techTimes) {
            this.title = title;
            this.twoSided = twoSided;
            this.techTimes = techTimes;
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

    private static final Logger log = LoggerFactory.getLogger(OutputTemplateAction.class);

    private String errorMessage;
    private OutputTemplate errorTemplate;

    private final TrainDiagram diagram;
    private final Settings settings;
    private final File outputDirectory;
    private final Collection<OutputTemplate> templates;

    public OutputTemplateAction(ActionContext context, TrainDiagram diagram, Settings settings, File outputDirectory, Collection<OutputTemplate> templates) {
        super(context);
        this.diagram = diagram;
        this.settings = settings;
        this.outputDirectory = outputDirectory;
        this.templates = templates;
    }

    @Override
    protected void backgroundAction() {
        long time = System.currentTimeMillis();
        setWaitMessage(ResourceLoader.getString("ot.message.wait"));
        setWaitDialogVisible(true);
        try {
            if (templates != null && !templates.isEmpty()) {
                try {
                    for (OutputTemplate template : templates) {
                        errorTemplate = template;
                        generateOutput(template);
                    }
                } catch (OutputException e) {
                    log.error(e.getMessage(), e);
                    errorMessage = e.getMessage();
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            errorMessage = ResourceLoader.getString("ot.message.error");
        } finally {
            setWaitDialogVisible(false);
        }
        time = System.currentTimeMillis() - time;
        log.debug("Generated in {}ms", time);
    }

    @Override
    protected void eventDispatchActionAfter() {
        if (errorMessage != null) {
            GuiComponentUtils.showError(errorTemplate.getName() + ": " + errorMessage, context.getLocationComponent());
        }
    }

    private List<OutputSetting> createOutputs(OutputTemplate template) {
        List<OutputSetting> result = null;
        if (template.getScript() != null) {
            final List<OutputSetting> out = new ArrayList<OutputSetting>();
            Map<String, Object> binding = new HashMap<String, Object>();
            binding.put("diagram", diagram);
            binding.put("outputs", new OutputCollector() {
                @Override
                public void add(String name, Map<String, Object> values) {
                    out.add(new OutputSetting(name, values, null));
                }

                @Override
                public void add(String name, Map<String, Object> values, String encoding) {
                    out.add(new OutputSetting(name, values, encoding));
                }
            });
            template.getScript().evaluate(binding);
            result = out;
        }
        return result;
    }

    private void generateOutput(OutputTemplate template) throws OutputException {
        String type = template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE, String.class);
        OutputFactory factory = OutputFactory.newInstance("groovy");
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
                for (TrainsCycle cycle : diagram.getCycles(TrainsCycleType.DRIVER_CYCLE)) {
                    this.generateOutput(
                            output,
                            this.getFile(template.getName() + "_" + cycle.getName(),
                                    template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class)),
                            textTemplate, type, cycle, null, null);
                }
            }
        } else {
            for(OutputSetting outputName : outputNames) {
                this.generateOutput(output, this.getFile(outputName.name), textTemplate, type, null, outputName.binding, outputName.encoding);
            }
        }
    }

    private void generateOutput(Output output, File outpuFile, TextTemplate textTemplate, String type,
            Object param, Map<String, Object> context, String encoding) throws OutputException {
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
        // nothing - starts, ends, stations, train_unit_cycles, engine_cycles
        if ("trains".equals(type) && param != null) {
            params.setParam("driver_cycle", param);
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
