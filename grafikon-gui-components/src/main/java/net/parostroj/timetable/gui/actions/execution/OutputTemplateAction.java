package net.parostroj.timetable.gui.actions.execution;

import java.io.File;
import java.util.*;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.OutputAction.Settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputTemplateAction extends EventDispatchAfterModelAction {

    private static final Logger log = LoggerFactory.getLogger(OutputTemplateAction.class);

    private String errorMessage;

    private final OutputAction outputAction;

    public OutputTemplateAction(ActionContext context, TrainDiagram diagram, Settings settings, File outputDirectory, Collection<OutputTemplate> templates) {
        super(context);
        this.outputAction = new OutputAction(diagram, settings, outputDirectory, templates);
    }

    @Override
    protected void backgroundAction() {
        long time = System.currentTimeMillis();
        setWaitMessage(ResourceLoader.getString("ot.message.wait"));
        setWaitDialogVisible(true);
        try {
            try {
                this.outputAction.execute();
            } catch (OutputException e) {
                log.error(e.getMessage(), e);
                errorMessage = e.getMessage();
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
            GuiComponentUtils.showError(this.outputAction.getErrorTemplate().getName() + ": " + errorMessage, context.getLocationComponent());
        }
    }
}
