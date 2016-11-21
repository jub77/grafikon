package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;

import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramPartFactory;

/**
 * Dialog for editing groups.
 *
 * @author jub
 */
public class EditGroupsDialog extends EditItemsDialog<Group, TrainDiagram> {

    public EditGroupsDialog(Window parent, boolean modal, boolean move, boolean edit, boolean newByName, boolean copy,
            boolean multiple) {
        super(parent, modal, move, edit, newByName, copy, multiple);
    }

    public static EditGroupsDialog newInstance(Window parent, boolean modal) {
        return newBuilder(EditGroupsDialog.class).setNewByName(true).build(parent, modal);
    }

    @Override
    protected Collection<Group> getList() {
        return element.getGroups();
    }

    @Override
    protected void add(Group item, int index) {
        // index ignored -> no move allowed
        element.getGroups().add(item);
    }

    @Override
    protected void remove(Group item) {
        element.getGroups().remove(item);
    }

    @Override
    protected void move(Group item, int oldIndex, int newIndex) {
        throw new IllegalStateException("Move not allowed");
    }

    @Override
    protected boolean deleteAllowed(Group item) {
        return true;
    }

    @Override
    protected Group createNew(String name) {
        TrainDiagramPartFactory factory = element.getPartFactory();
        Group newGroup = factory.createGroup(factory.createId());
        newGroup.setName(name);
        return newGroup;
    }
}
