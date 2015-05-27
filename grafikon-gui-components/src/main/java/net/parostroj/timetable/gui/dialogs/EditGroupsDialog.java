package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;

import net.parostroj.timetable.model.Group;

/**
 * Dialog for editing groups.
 *
 * @author jub
 */
public class EditGroupsDialog extends EditItemsDialog<Group> {

    public EditGroupsDialog(Window parent, boolean modal) {
        super(parent, modal, false, false);
    }

    @Override
    protected Collection<Group> getList() {
        return diagram.getGroups();
    }

    @Override
    protected void add(Group item, int index) {
        diagram.addGroup(item, index);
    }

    @Override
    protected void remove(Group item) {
        diagram.removeGroup(item);
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
        Group newGroup = diagram.createGroup(diagram.createId());
        newGroup.setName(name);
        return newGroup;
    }
}
