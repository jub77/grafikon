package net.parostroj.timetable.gui.components;

import java.util.*;

import javax.swing.JComboBox;

import net.parostroj.timetable.gui.components.GroupSelect.Type;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Combo box for group selection.
 *
 * @author jub
 */
public class GroupsComboBox extends JComboBox<Wrapper<Group>> {

    private final Wrapper<Group> all = Wrapper.getPrototypeWrapper("<" + ResourceLoader.getString("groups.all") + ">");
    private final Wrapper<Group> none = Wrapper.getPrototypeWrapper("<" + ResourceLoader.getString("groups.none") + ">");
    private final boolean allOption;

    /**
     * @param allOption if the all option is available
     */
    public GroupsComboBox(boolean allOption) {
        super();
        this.allOption = allOption;
    }

    /**
     * updates combobox. No group means none.
     *
     * @param diagram diagram
     * @param selection selected group (null -> none)
     */
    public void updateGroups(TrainDiagram diagram, Group selection) {
        updateGroups(diagram, selection == null ? new GroupSelect(Type.NONE) : new GroupSelect(Type.GROUP, selection));
    }

    /**
     * updates combobox.
     *
     * @param diagram diagram
     * @param type type
     * @param selection selected group (depends on type)
     */
    public void updateGroups(TrainDiagram diagram, GroupSelect groupSelect) {
        if (groupSelect.getType() == Type.ALL && !allOption)
            throw new IllegalArgumentException("Cannot set ALL.");
        // clear
        removeAllItems();
        // add no and all
        if (allOption) {
            addItem(all);
        }
        addItem(none);
        List<Group> groups = new ArrayList<Group>(diagram.getGroups().toCollection());
        this.sortGroups(groups);
        for (Group group : groups) {
            this.addItem(new Wrapper<Group>(group));
        }
        switch (groupSelect.getType()) {
            case ALL:
                setSelectedItem(all);
                break;
            case NONE:
                setSelectedItem(none);
                break;
            default:
                Wrapper<Group> w = new Wrapper<Group>(groupSelect.getGroup());
                setSelectedItem(w);
                break;
        }
    }

    private void sortGroups(List<Group> groups) {
        Collections.sort(groups, new Comparator<Group>() {
            public int compare(Group o1, Group o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    /**
     * @return selected group (type denotes special cases - all, none)
     */
    public GroupSelect getGroupSelection() {
        Wrapper<?> selected = (Wrapper<?>) getSelectedItem();
        Type type = selected.equals(all) ? Type.ALL : (selected.equals(none) ? Type.NONE : Type.GROUP);
        return new GroupSelect(type, (Group) (type == Type.GROUP ? selected.getElement() : null));
    }
}
