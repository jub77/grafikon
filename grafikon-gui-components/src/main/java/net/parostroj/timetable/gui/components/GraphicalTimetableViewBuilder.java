package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.EventDispatchAfterModelAction;
import net.parostroj.timetable.gui.actions.execution.ModelAction;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.gui.events.DiagramChangeMessage;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.output2.DrawParams;
import net.parostroj.timetable.output2.Output;
import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.OutputFactory;
import net.parostroj.timetable.output2.gt.FileOutputType;
import net.parostroj.timetable.output2.gt.GTDraw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GraphicalTimetableViewBuilder {

    private static final Logger log = LoggerFactory.getLogger(GraphicalTimetableViewBuilder.class);

    private final Map<GTViewSettings.Key, Object> settingOverlay = new EnumMap<>(GTViewSettings.Key.class);
    private Mediator mediator;
    private boolean save;
    private boolean forTrains;

    GraphicalTimetableViewBuilder() {}

    public GraphicalTimetableViewBuilder withSave() {
        this.save = true;
        return this;
    }

    public GraphicalTimetableViewBuilder forTrains(Mediator mediator) {
        this.forTrains = true;
        this.mediator = mediator;
        return this;
    }

    public GraphicalTimetableViewBuilder withSettings(GTViewSettings.Key key, Object value) {
        this.settingOverlay.put(key, value);
        return this;
    }

    public GraphicalTimetableView build() {
        GraphicalTimetableView view = new GraphicalTimetableView();
        if (save) {
            updateWithSave(view);
        }
        if (forTrains) {
            this.addForTrains(view);
        }
        if (!settingOverlay.isEmpty()) {
            this.updateSettings(view);
        }
        return view;
    }

    private void updateSettings(GraphicalTimetableView view) {
        GTViewSettings settings = view.getSettings();
        settingOverlay.forEach(settings::set);
        view.setSettings(settings);
    }

    private void addForTrains(GraphicalTimetableView view) {
        HighlightSelectTrains hts = new HighlightSelectTrains(mediator, Color.GREEN, view);
        view.setParameter(GTDraw.HIGHLIGHTED_TRAINS, hts);
        view.setRegionSelector(hts, TimeInterval.class);
        this.mediator.addColleague(
                message -> view.setTrainDiagram(((DiagramChangeMessage) message).diagram()),
                DiagramChangeMessage.class);
    }

    public static void updateWithSave(GraphicalTimetableView view) {
        // extend context menu
        JMenuItem saveMenuItem = new JMenuItem(ResourceLoader.getString("gt.save"));
        view.popupMenu.add(new JSeparator());
        view.popupMenu.add(saveMenuItem);
        // action
        saveMenuItem.addActionListener(evt -> {
            if (view.getRoute() == null) {
                return;
            }
            SaveImageDialog dialog = new SaveImageDialog((Frame) view.getTopLevelAncestor(), true);
            dialog.setLocationRelativeTo(view.getParent());
            dialog.setSaveSize(view.getSize());
            dialog.setVisible(true);

            if (!dialog.isSave()) {
                return;
            }

            // save action
            ActionContext actionContext = new ActionContext(GuiComponentUtils.getTopLevelComponent(view));
            ModelAction action = new EventDispatchAfterModelAction(actionContext) {

                private boolean error;

                @Override
                protected void backgroundAction() {
                    setWaitMessage(ResourceLoader.getString("wait.message.image.save"));
                    setWaitDialogVisible(true);
                    long time = System.currentTimeMillis();
                    try {
                        Dimension saveSize = dialog.getSaveSize();
                        // get values and provide save
                        GTViewSettings config = view.getSettings();
                        config.setOption(GTViewSettings.Key.DISABLE_STATION_NAMES, Boolean.FALSE);
                        GTDraw draw = view.createDraw(config, saveSize);

                        try {
                            OutputFactory factory = OutputFactory.newInstance("draw");
                            Output output = factory.createOutput("diagram");

                            output.write(output.getAvailableParams()
                                    .setParam(Output.PARAM_OUTPUT_FILE, dialog.getSaveFile())
                                    .setParam(Output.PARAM_TRAIN_DIAGRAM, view.diagram)
                                    .setParam(DrawParams.GT_DRAWS, List.of(draw))
                                    .setParam(DrawParams.OUTPUT_TYPE, getOutputType()));
                        } catch (OutputException e) {
                            log.warn("Error saving file: {}", dialog.getSaveFile(), e);
                            error = true;
                        }
                    } finally {
                        log.debug("Image save finished in {}ms", System.currentTimeMillis() - time);
                        setWaitDialogVisible(false);
                    }
                }

                private FileOutputType getOutputType() {
                    return switch (dialog.getImageType()) {
                        case PNG -> FileOutputType.PNG;
                        case PDF -> FileOutputType.PDF;
                        case SVG -> FileOutputType.SVG;
                    };
                }

                @Override
                protected void eventDispatchActionAfter() {
                    if (error) {
                        JOptionPane.showMessageDialog(context.getLocationComponent(), ResourceLoader.getString("save.image.error"), ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            ActionHandler.getInstance().execute(action);
        });
    }
}
