package net.parostroj.timetable.gui.actions.execution;

import java.io.File;
import java.util.Collection;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;

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
    private void generateOutput(OutputTemplate template) throws OutputException {
        String type = (String) template.getAttribute(OutputTemplate.ATTR_OUTPUT_TYPE);
        OutputFactory factory = OutputFactory.newInstance("groovy");
        Output output = factory.createOutput(type);
        generateOutput(output, this.getFile(template.getName()), template.getTemplate(), type, null);
        if ("trains".equals(type)) {
            // for each driver circulation
            for (TrainsCycle cycle : diagram.getCycles(TrainsCycleType.DRIVER_CYCLE)) {
                generateOutput(output, this.getFile(template.getName() + "_" + cycle.getName()), template.getTemplate(), type, cycle);
            }
        }
    }

    private void generateOutput(Output output, File outpuFile, TextTemplate textTemplate, String type, Object param) throws OutputException {
        OutputParams params = settings.createParams();
        params.setParam(DefaultOutputParam.TEXT_TEMPLATE, textTemplate);
        params.setParam(DefaultOutputParam.TRAIN_DIAGRAM, diagram);
        params.setParam(DefaultOutputParam.OUTPUT_FILE, outpuFile);
        // nothing - starts, ends, stations, train_unit_cycles, engine_cycles
        if ("trains".equals(type) && param != null) {
            params.setParam("driver_cycle", param);
        }
        output.write(params);
    }

    private File getFile(String name) {
        name = name.replaceAll("[\\\\:/\"?<>|]", "");
        return new File(outputDirectory, name + ".html");
    }
}
