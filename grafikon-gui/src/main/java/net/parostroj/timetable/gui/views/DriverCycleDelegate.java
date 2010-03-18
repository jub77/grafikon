/*
 * DriverCycleDelegate.java
 * 
 * Created on 16.9.2007, 15:35:44
 */
package net.parostroj.timetable.gui.views;

import java.util.List;
import javax.swing.JComponent;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.dialogs.TCDetailsViewDialog;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.utils.Tuple;

/**
 * Implementation of the interface for driver cycle.
 * 
 * @author jub
 */
public class DriverCycleDelegate implements TCDelegate {
    
    private TCDetailsViewDialog editDialog;

    public DriverCycleDelegate() {
    }

    @Override
    public void setSelectedCycle(ApplicationModel model, TrainsCycle cycle) {
        model.setSelectedDriverCycle(cycle);
    }

    @Override
    public TrainsCycle getSelectedCycle(ApplicationModel model) {
        return model.getSelectedDriverCycle();
    }

    @Override
    public void fireEvent(Action action, ApplicationModel model, TrainsCycle cycle) {
        ApplicationModelEvent event = null;
        switch (action) {
        case SELECTED_CHANGED:
            event = new ApplicationModelEvent(ApplicationModelEventType.SELECTED_DRIVER_CYCLE_CHANGED, model, cycle);
            break;
        case DELETE_CYCLE:
            event = new ApplicationModelEvent(ApplicationModelEventType.DELETE_DRIVER_CYCLE, model, cycle);
            break;
        case MODIFIED_CYCLE:
            event = new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_DRIVER_CYCLE, model, cycle);
            break;
        case NEW_CYCLE:
            event = new ApplicationModelEvent(ApplicationModelEventType.NEW_DRIVER_CYCLE, model, cycle);
            break;
        }
        if (event != null)
            model.fireEvent(event);
    }

    @Override
    public Action transformEventType(ApplicationModelEventType type) {
        switch (type) {
        case DELETE_DRIVER_CYCLE:
            return TCDelegate.Action.DELETE_CYCLE;
        case NEW_DRIVER_CYCLE:
            return TCDelegate.Action.NEW_CYCLE;
        case MODIFIED_DRIVER_CYCLE:
            return TCDelegate.Action.MODIFIED_CYCLE;
        case SELECTED_DRIVER_CYCLE_CHANGED:
            return TCDelegate.Action.SELECTED_CHANGED;
        }
        return null;
    }

    @Override
    public String getTrainCycleErrors(TrainsCycle cycle, TrainDiagram diagram) {
        StringBuilder result = new StringBuilder();
        List<Tuple<TrainsCycleItem>> conflicts = cycle.checkConflicts();
        for (Tuple<TrainsCycleItem> item : conflicts) {
            if (item.first.getToInterval().getOwnerAsNode() != item.second.getFromInterval().getOwnerAsNode()) {
                // get time difference
                int difference = item.second.getStartTime() - item.first.getEndTime();
                Integer okDifference = (Integer)diagram.getAttribute("station.transfer.time");
                String template = ResourceLoader.getString("ec.move.nodes");
                if (okDifference != null) {
                    // computed difference in model seconds
                    int computedDiff = (int)(okDifference.intValue() * ((Double)diagram.getAttribute("time.scale")).doubleValue() * 60);
                    if (difference < computedDiff)
                        template = ResourceLoader.getString("ec.move.nodes.time.problem");
                }
                result.append(String.format(template,item.first.getTrain().getName(),item.first.getToInterval().getOwnerAsNode().getName(),item.second.getTrain().getName(),item.second.getFromInterval().getOwnerAsNode().getName()));
                result.append("\n");
            }
            if (item.first.getEndTime() >= item.second.getStartTime()) {
                result.append(String.format(ResourceLoader.getString("ec.problem.time"),item.first.getTrain().getName(),TimeConverter.convertFromIntToText(item.first.getEndTime()),item.second.getTrain().getName(),TimeConverter.convertFromIntToText(item.second.getStartTime())));
                result.append("\n");
            }
        }
        return result.toString();
    }

    @Override
    public List<TrainsCycleItem> getTrainCycles(Train train) {
        return train.getCycles(TrainsCycleType.DRIVER_CYCLE);
    }

    @Override
    public TrainsCycleType getType() {
        return TrainsCycleType.DRIVER_CYCLE;
    }

    @Override
    public void showEditDialog(JComponent component, ApplicationModel model) {
        if (editDialog == null)
            editDialog = new TCDetailsViewDialog((java.awt.Frame)component.getTopLevelAncestor(), true);
        editDialog.setLocationRelativeTo(component);
        editDialog.updateValues(this, model);
        editDialog.setVisible(true);
    }

    @Override
    public String getCycleDescription(ApplicationModel model) {
        return getSelectedCycle(model).getDescription();
    }

    @Override
    public boolean isOverlappingEnabled() {
        return false;
    }
}
