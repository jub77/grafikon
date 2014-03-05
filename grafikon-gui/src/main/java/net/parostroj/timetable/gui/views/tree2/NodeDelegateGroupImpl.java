package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Node with group.
 *
 * @author jub
 */
public class NodeDelegateGroupImpl implements NodeDelegate {

    @Override
    public String getNodeText(TrainTreeNode trainTreeNode) {
        Group group = (Group) trainTreeNode.getUserObject();
        return group == null ? "<" + ResourceLoader.getString("groups.none") + ">" : group.getName();
    }

    @Override
    public Object getUserObject(Train train) {
        return train.getAttribute(Train.ATTR_GROUP);
    }

    @Override
    public boolean isNode(TrainTreeNode trainTreeNode, Train train) {
        Group trainGroup = train.getAttributes().get(Train.ATTR_GROUP, Group.class);
        Group nodeGroup = (Group) trainTreeNode.getUserObject();
        return trainGroup == nodeGroup;
    }
}
