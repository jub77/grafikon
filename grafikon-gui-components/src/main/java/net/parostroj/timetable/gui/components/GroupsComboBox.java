package net.parostroj.timetable.gui.components;

import javax.swing.JComboBox;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Pair;

/**
 * Combo box for group selection.
 *
 * @author jub
 */
public class GroupsComboBox extends JComboBox {

    public static enum Type {
        ALL, NONE, GROUP
    }

    private final Wrapper<String> all = new Wrapper<String>("<" + ResourceLoader.getString("groups.all") + ">");
    private final Wrapper<String> none = new Wrapper<String>("<" + ResourceLoader.getString("groups.none") + ">");
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
        updateGroups(diagram, selection == null ? Type.NONE : Type.GROUP, selection);
    }

    /**
     * updates combobox.
     *
     * @param diagram diagram
     * @param type type
     * @param selection selected group (depends on type)
     */
    public void updateGroups(TrainDiagram diagram, Type type, Group selection) {
        if (type == Type.ALL && !allOption)
            throw new IllegalArgumentException("Cannot set ALL.");
        // clear
        removeAllItems();
        // add no and all
        if (allOption)
            addItem(all);
        addItem(none);
        for (Group group : diagram.getGroups()) {
            addItem(new Wrapper<Group>(group));
        }
        switch (type) {
            case ALL:
                setSelectedItem(all);
                break;
            case NONE:
                setSelectedItem(none);
                break;
            default:
                Wrapper<Group> w = new Wrapper<Group>(selection);
                setSelectedItem(w);
                break;
        }
    }

    /**
     * @return selected group (type denotes special cases - all, none)
     */
    public Pair<Type, Group> getSelectedGroup() {
        Wrapper<?> selected = (Wrapper<?>) getSelectedItem();
        Type type = selected.equals(all) ? Type.ALL : (selected.equals(none) ? Type.NONE : Type.GROUP);
        return new Pair<GroupsComboBox.Type, Group>(type, (Group) (type == Type.GROUP ? selected.getElement() : null));
    }
}
