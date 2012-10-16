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

    public static enum Type {ALL, NONE, GROUP}

    private final Wrapper<String> all = new Wrapper<String>("<" + ResourceLoader.getString("groups.all") + ">");
    private final Wrapper<String> none = new Wrapper<String>("<" + ResourceLoader.getString("groups.none") + ">");
    private final boolean allOption;

    public GroupsComboBox(boolean allOption) {
        super();
        this.allOption = allOption;
    }

    public void updateGroups(TrainDiagram diagram, Group selection) {
        updateGroups(diagram, selection == null ? Type.NONE : Type.GROUP, selection);
    }

    public void updateGroups(TrainDiagram diagram, Type type, Group selection) {
        // clear
        removeAllItems();
        // add no and all
        if (allOption)
            addItem(all);
        addItem(none);
        for (Group group : diagram.getGroups()) {
            addItem(new Wrapper<Group>(group));
        }
    }

    public Pair<Type, Group> getSelectedGroup() {
        // missing implementation
        return null;
    }
}
