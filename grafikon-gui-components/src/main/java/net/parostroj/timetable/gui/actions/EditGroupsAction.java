package net.parostroj.timetable.gui.actions;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import net.parostroj.timetable.gui.dialogs.GroupsDialog;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Reference;

/**
 * Edit groups action.
 *
 * @author jub
 */
public class EditGroupsAction extends AbstractAction {

	private Reference<TrainDiagram> diagramReference;

	/**
	 * @param ref reference to train diagram
	 */
	public EditGroupsAction(Reference<TrainDiagram> ref) {
		this.diagramReference = ref;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        Window parent = SwingUtilities.getWindowAncestor((Component) e.getSource());
		GroupsDialog dialog = new GroupsDialog(parent, true);
        dialog.setLocationRelativeTo(parent);
        dialog.showDialog(diagramReference.get());
        dialog.dispose();
	}
}
