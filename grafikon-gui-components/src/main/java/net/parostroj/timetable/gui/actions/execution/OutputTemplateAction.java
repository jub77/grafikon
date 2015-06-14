package net.parostroj.timetable.gui.actions.execution;

import java.io.File;
import java.util.*;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.OutputWriter.Settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputTemplateAction extends EventDispatchAfterModelAction {

    private static final Logger log = LoggerFactory.getLogger(OutputTemplateAction.class);

    private String errorMessage;

    private final OutputWriter outputAction;
    private final int count;

    private int current;

    public OutputTemplateAction(ActionContext context, TrainDiagram diagram, Settings settings, File outputDirectory, Collection<OutputTemplate> templates) {
        super(context);
        this.outputAction = new OutputWriter(diagram, settings, outputDirectory, templates);
        this.count = templates.size();
    }

    @Override
    protected void backgroundAction() {
        long time = System.currentTimeMillis();
        String waitMessage = ResourceLoader.getString("ot.message.wait");
        setWaitMessage(waitMessage);
        setWaitDialogVisible(true);
        context.setShowProgress(count > 1);
        current = 0;
        try {
            try {
                this.outputAction.setListener(template -> {
                    current++;
                    if (count > 1) {
                        setProgressMessage(String.format("%s (%d/%d)", template.getName(), current, count));
                        setWaitProgress(100 * current / count);
                    }
                });
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
