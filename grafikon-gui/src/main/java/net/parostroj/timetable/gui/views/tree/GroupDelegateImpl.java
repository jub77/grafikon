package net.parostroj.timetable.gui.views.tree;

import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.ResourceLoader;

public class GroupDelegateImpl extends CategoryDelegateImpl<Group> {

    public GroupDelegateImpl(boolean containTrains) {
        super(containTrains);
    }

    public GroupDelegateImpl(boolean containTrains, TrainTreeNodeSort sort) {
        super(containTrains, sort);
    }

    @Override
    public String getNodeText(Group item) {
        return item == null ? ResourceLoader.getString("groups.none") : item.getName();
    }

    @Override
    protected boolean belongs(Train train, Group item) {
        return train.getAttributes().get(Train.ATTR_GROUP) == item;
    }
}
