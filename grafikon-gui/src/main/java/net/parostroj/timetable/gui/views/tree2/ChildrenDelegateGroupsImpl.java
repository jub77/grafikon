package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.Group;

/**
 * Delegate for groups.
 *
 * @author jub
 */
public class ChildrenDelegateGroupsImpl extends ChildrenDelegateImpl implements ChildrenDelegate {

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public int addChildNode(TrainTreeNode node, TrainTreeNode newChildNode) {
        for (int i = 0; i < node.getChildCount(); i++) {
            TrainTreeNode childNode = (TrainTreeNode) node.getChildAt(i);
            if (this.compareGroups(this.getGroupFromNode(newChildNode), this.getGroupFromNode(childNode)) < 0) {
                node.insert(newChildNode, i);
                return i;
            }
        }
        node.add(newChildNode);
        return node.getChildCount() - 1;
    }

    private int compareGroups(Group group1, Group group2) {
        if (group1 == group2)
            return 0;
        else if (group1 == null)
            return -1;
        else if (group2 == null)
            return 1;
        else {
            return group1.getName().compareTo(group2.getName());
        }
    }

    private Group getGroupFromNode(TrainTreeNode node) {
        return (Group) node.getUserObject();
    }
}
