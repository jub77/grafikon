/*
 * DriverCycleDelegate.java
 *
 * Created on 16.9.2007, 15:35:44
 */
package net.parostroj.timetable.gui.views;

import java.util.List;

import javax.swing.JComponent;

import net.parostroj.timetable.actions.TrainsCycleChecker;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.dialogs.TCDetailsViewDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Implementation of the interface for driver cycle.
 *
 * @author jub
 */
public class DriverCycleDelegate extends TCDelegate {

    private TCDetailsViewDialog editDialog;

    public DriverCycleDelegate(ApplicationModel model) {
        super(model, new TrainsCycleChecker());
    }

    @Override
    public String getTrainCycleErrors(TrainsCycle cycle) {
        TrainDiagram diagram = model.getDiagram();
        StringBuilder result = new StringBuilder();
        List<TrainsCycleChecker.Conflict> conflicts = checker.checkConflicts(cycle);
        for (TrainsCycleChecker.Conflict item : conflicts) {
            TrainsCycleItem fromItem = item.getFrom();
            TrainsCycleItem toItem = item.getTo();
            if (fromItem.getToInterval().getOwnerAsNode() != toItem.getFromInterval().getOwnerAsNode()) {
                addNewLineIfNotEmpty(result);
                // get time difference
                int difference = toItem.getNormalizedStartTime() - fromItem.getNormalizedEndTime();
                Integer okDifference = diagram.getAttribute(TrainDiagram.ATTR_STATION_TRANSFER_TIME, Integer.class);
                String template = ResourceLoader.getString("ec.move.nodes");
                if (okDifference != null) {
                    // computed difference in model seconds
                    int computedDiff = (int) (okDifference.intValue()
                            * diagram.getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class).doubleValue() * 60);
                    if (difference < computedDiff) {
                        template = ResourceLoader.getString("ec.move.nodes.time.problem");
                    }
                }
                result.append(String.format(template, fromItem.getTrain().getName(),
                        fromItem.getToInterval().getOwnerAsNode().getName(), toItem.getTrain().getName(),
                        toItem.getFromInterval().getOwnerAsNode().getName()));
            }
            if (fromItem.getNormalizedEndTime() >= toItem.getNormalizedStartTime()) {
                addNewLineIfNotEmpty(result);
                TimeConverter c = diagram.getTimeConverter();
                result.append(String.format(ResourceLoader.getString("ec.problem.time"), fromItem.getTrain().getName(),
                        c.convertIntToText(fromItem.getEndTime()), toItem.getTrain().getName(),
                        c.convertIntToText(toItem.getStartTime())));
            }
        }
        return result.toString();
    }

    @Override
    public void showEditDialog(JComponent component) {
        if (editDialog == null) {
            editDialog = new TCDetailsViewDialog(GuiComponentUtils.getWindow(component), true);
        }
        editDialog.setLocationRelativeTo(component);
        editDialog.updateValues(this);
        editDialog.setVisible(true);
    }

    @Override
    public TrainsCycleType getType() {
        return model.getDiagram() != null ? model.getDiagram().getDriverCycleType() : null;
    }
}
