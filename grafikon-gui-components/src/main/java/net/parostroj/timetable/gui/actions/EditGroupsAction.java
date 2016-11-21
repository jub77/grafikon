package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.parostroj.timetable.gui.dialogs.EditGroupsDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Reference;

/**
 * Edit groups action.
 *
 * @author jub
 */
public class EditGroupsAction extends AbstractAction {

    private final Reference<TrainDiagram> diagramReference;

    /**
     * @param ref reference to train diagram
     */
    public EditGroupsAction(Reference<TrainDiagram> ref) {
        this.diagramReference = ref;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Window parent = GuiComponentUtils.getWindow((Component) e.getSource());
        EditGroupsDialog dialog = EditGroupsDialog.newInstance(parent, true);
        dialog.setLocationRelativeTo(parent);
        dialog.showDialog(diagramReference.get());
        dialog.dispose();
    }
}
