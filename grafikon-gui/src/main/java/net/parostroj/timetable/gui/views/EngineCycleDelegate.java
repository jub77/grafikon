/*
 * EngineCycleDelegate.java
 *
 * Created on 16.9.2007, 15:35:44
 */
package net.parostroj.timetable.gui.views;

import javax.swing.JComponent;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.dialogs.TCDetailsViewDialogEngineClass;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.TrainsCycleType;

/**
 * Implementation of the interface for engine cycle.
 *
 * @author jub
 */
public class EngineCycleDelegate extends TCDelegate {

    private TCDetailsViewDialogEngineClass editDialog;

    public EngineCycleDelegate(ApplicationModel model) {
        super(model);
    }

    @Override
    public void showEditDialog(JComponent component) {
        if (editDialog == null) {
            editDialog = new TCDetailsViewDialogEngineClass(GuiComponentUtils.getWindow(component), true);
        }
        editDialog.setLocationRelativeTo(component);
        editDialog.updateValues(this, model.getDiagram());
        editDialog.setVisible(true);
    }

    @Override
    public TrainsCycleType getType() {
        return model.getDiagram() != null ? model.getDiagram().getEngineCycleType() : null;
    }
}
