/*
 * TrainUnitCycleDelegate.java
 *
 * Created on 16.9.2007, 15:35:44
 */
package net.parostroj.timetable.gui.views;

import net.parostroj.timetable.gui.dialogs.TCDetailsViewDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.mediator.Mediator;
import net.parostroj.timetable.model.TrainsCycleType;

import javax.swing.*;

/**
 * Implementation of the interface for train unit cycle.
 *
 * @author jub
 */
public class TrainUnitCycleDelegate extends TCDelegate {

    private TCDetailsViewDialog editDialog;

    public TrainUnitCycleDelegate(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void showEditDialog(JComponent component) {
        if (editDialog == null) {
            editDialog = new TCDetailsViewDialog(GuiComponentUtils.getWindow(component), true);
        }
        editDialog.setLocationRelativeTo(component);
        editDialog.updateValues(this, diagram);
        editDialog.setVisible(true);
    }

    @Override
    public TrainsCycleType getType() {
        return diagram != null ? diagram.getTrainUnitCycleType() : null;
    }
}
