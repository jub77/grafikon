package net.parostroj.timetable.gui.components;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.*;

import net.parostroj.timetable.gui.actions.execution.*;
import net.parostroj.timetable.gui.components.GTViewSettings.Key;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.gt.GTDraw;
import net.parostroj.timetable.output2.gt.GTDrawParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GT view with save dialog.
 *
 * @author jub
 */
public class GraphicalTimetableViewWithSave extends GraphicalTimetableView {

    private static final Logger log = LoggerFactory.getLogger(GraphicalTimetableViewWithSave.class);
    private SaveImageDialog dialog;

    public GraphicalTimetableViewWithSave() {
        super();

        // extend context menu
        JMenuItem saveMenuItem = new JMenuItem(ResourceLoader.getString("gt.save"));
        popupMenu.add(new JSeparator());
        popupMenu.add(saveMenuItem);
        // action
        saveMenuItem.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveMenuItemActionPerformed(e);
            }
        });
    }

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.getRoute() == null) {
            return;
        }
        if (dialog == null)
            dialog = new SaveImageDialog((Frame)this.getTopLevelAncestor(), true);
        dialog.setLocationRelativeTo(this.getParent());
        dialog.setSaveSize(this.getSize());
        dialog.setVisible(true);

        if (!dialog.isSave()) {
            return;
        }

        // save action
        ActionContext actionContext = new ActionContext(GuiComponentUtils.getTopLevelComponent(this));
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
                    GTViewSettings config = getSettings();
                    config.setOption(Key.DISABLE_STATION_NAMES, Boolean.FALSE);
                    TrainDiagram diagram = getDiagram();
                    if (diagram != null) {
                        Integer from = diagram.getAttribute(TrainDiagram.ATTR_FROM_TIME, Integer.class);
                        Integer to = diagram.getAttribute(TrainDiagram.ATTR_TO_TIME, Integer.class);
                        config.set(GTViewSettings.Key.START_TIME, from);
                        config.set(GTViewSettings.Key.END_TIME, to);
                    }
                    config.set(GTViewSettings.Key.SIZE, saveSize);
                    config.remove(GTViewSettings.Key.HIGHLIGHTED_TRAINS);

                    try {
                        OutputFactory factory = OutputFactory.newInstance("draw");
                        Output output = factory.createOutput("diagram");

                        GTDrawParams gtParams = new GTDrawParams(config.getGTDrawType(),
                                config.createGTDrawSettings(),
                                dialog.getImageType() == SaveImageDialog.Type.PNG ? GTDraw.OutputType.PNG : GTDraw.OutputType.SVG);

                        output.write(output.getAvailableParams().setParam(DefaultOutputParam.OUTPUT_FILE, dialog.getSaveFile())
                                .setParam(DefaultOutputParam.TRAIN_DIAGRAM, diagram).setParam(DrawParams.GT_PARAMS, gtParams)
                                .setParam(DrawParams.ROUTES_PARAM, Arrays.asList(getRoute())));
                    } catch (OutputException e) {
                        log.warn("Error saving file: " + dialog.getSaveFile(), e);
                        error = true;
                    }
                } finally {
                    log.debug("Image save finished in {}ms", System.currentTimeMillis() - time);
                    setWaitDialogVisible(false);
                }
            }

            @Override
            protected void eventDispatchActionAfter() {
                if (error) {
                    JOptionPane.showMessageDialog(context.getLocationComponent(), ResourceLoader.getString("save.image.error"), ResourceLoader.getString("save.image.error.text"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        ActionHandler.getInstance().execute(action);
    }
}
