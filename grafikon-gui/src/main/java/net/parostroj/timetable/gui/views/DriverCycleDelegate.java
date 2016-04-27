/*
 * DriverCycleDelegate.java
 *
 * Created on 16.9.2007, 15:35:44
 */
package net.parostroj.timetable.gui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import net.parostroj.timetable.actions.TrainsCycleChecker;
import net.parostroj.timetable.actions.TrainsCycleChecker.ConflictType;
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
        super(model, new TrainsCycleChecker(ConflictType.NODE, ConflictType.TIME, ConflictType.SETUP_TIME, ConflictType.TRANSITION_TIME));
    }

    @Override
    public String getTrainCycleErrors(TrainsCycle cycle) {
        TrainDiagram diagram = model.getDiagram();
        TimeConverter c = diagram.getTimeConverter();
        StringBuilder result = new StringBuilder();
        List<TrainsCycleChecker.Conflict> conflicts = checker.checkConflicts(cycle);
        for (TrainsCycleChecker.Conflict item : conflicts) {
            TrainsCycleItem fromItem = item.getFrom();
            TrainsCycleItem toItem = item.getTo();
            for (ConflictType conflictType : this.colapseTimeConflictTypes(item.getType())) {
                switch (conflictType) {
                case NODE:
                    addNewLineIfNotEmpty(result);
                    result.append(String.format("%s %s", ResourceLoader.getString("ec.move.nodes"), formatItems(fromItem, toItem, c)));
                    break;
                case TRANSITION_TIME:
                    addNewLineIfNotEmpty(result);
                    result.append(String.format("%s %s", ResourceLoader.getString("ec.move.nodes.time.problem"), formatItems(fromItem, toItem, c)));
                    break;
                case TIME: case SETUP_TIME:
                    addNewLineIfNotEmpty(result);
                    result.append(String.format("%s %s", ResourceLoader.getString("ec.problem.time"), formatItems(fromItem, toItem, c)));
                    break;
                }
            }
        }
        return result.toString();
    }

    private Collection<ConflictType> colapseTimeConflictTypes(Collection<ConflictType> types) {
        Collection<ConflictType> result = new ArrayList<>(types);
        if (result.contains(ConflictType.TIME)) {
            result.remove(ConflictType.SETUP_TIME);
            result.remove(ConflictType.TRANSITION_TIME);
        }
        if (result.contains(ConflictType.SETUP_TIME)) {
            result.remove(ConflictType.TRANSITION_TIME);
        }
        return result;
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
