package net.parostroj.timetable.gui.actions.execution;

import java.io.File;
import java.util.*;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.utils.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputTemplateAction extends EventDispatchAfterModelAction {

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

    private static final Logger LOG = LoggerFactory.getLogger(OutputTemplateAction.class);

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
                    LOG.error(e.getMessage(), e);
                    errorMessage = e.getMessage();
                }
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            errorMessage = ResourceLoader.getString("ot.message.error");
        } finally {
            setWaitDialogVisible(false);
        }
        time = System.currentTimeMillis() - time;
        LOG.debug("Generated in {}ms", time);
    }

    @Override
    protected void eventDispatchActionAfter() {
        if (errorMessage != null) {
            ActionUtils.showError(errorTemplate.getName() + ": " + errorMessage, context.getLocationComponent());
        }
    }

    private List<Pair<String, Map<String, Object>>> createOutputs(OutputTemplate template) {
        List<Pair<String, Map<String, Object>>> result = null;
        if (template.getScript() != null) {
            final List<Pair<String, Map<String, Object>>> out = new ArrayList<Pair<String, Map<String, Object>>>();
            Map<String, Object> binding = new HashMap<String, Object>();
            binding.put("diagram", diagram);
            binding.put("outputs", new Object() {
                @SuppressWarnings("unused")
                public void add(String name, Map<String, Object> values) {
                    out.add(new Pair<String, Map<String, Object>>(name, values));
                }
            });
            template.getScript().evaluate(binding);
            result = out;
        }
        return result;
    }

    private void generateOutput(OutputTemplate template) throws OutputException {
        String type = (String) template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE);
        OutputFactory factory = OutputFactory.newInstance("groovy");
        Output output = factory.createOutput(type);
        TextTemplate textTemplate = template.getAttribute(OutputTemplate.ATTR_DEFAULT_TEMPLATE) == Boolean.TRUE ? null : template.getTemplate();
        List<Pair<String, Map<String, Object>>> outputNames = this.createOutputs(template);
        if (outputNames == null) {
            this.generateOutput(
                    output,
                    this.getFile(template.getName(),
                            template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class)),
                    textTemplate, type, null, null);
            if ("trains".equals(type)) {
                // for each driver circulation
                for (TrainsCycle cycle : diagram.getCycles(TrainsCycleType.DRIVER_CYCLE)) {
                    this.generateOutput(
                            output,
                            this.getFile(template.getName() + "_" + cycle.getName(),
                                    template.getAttributes().get(OutputTemplate.ATTR_OUTPUT_EXTENSION, String.class)),
                            textTemplate, type, cycle, null);
                }
            }
        } else {
            for(Pair<String, Map<String, Object>> outputName : outputNames) {
                this.generateOutput(output, this.getFile(outputName.first), textTemplate, type, null, outputName.second);
            }
        }
    }

    private void generateOutput(Output output, File outpuFile, TextTemplate textTemplate, String type,
            Object param, Map<String, Object> context) throws OutputException {
        OutputParams params = settings.createParams();
        if (textTemplate != null) {
            params.setParam(DefaultOutputParam.TEXT_TEMPLATE, textTemplate);
        }
        params.setParam(DefaultOutputParam.TRAIN_DIAGRAM, diagram);
        params.setParam(DefaultOutputParam.OUTPUT_FILE, outpuFile);
        if (context != null) {
            params.setParam(DefaultOutputParam.CONTEXT, context);
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
