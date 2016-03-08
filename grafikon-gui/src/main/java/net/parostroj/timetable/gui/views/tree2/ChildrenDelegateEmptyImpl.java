package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.Train;

/**
 * Leaf - no children.
 *
 * @author jub
 */
public class ChildrenDelegateEmptyImpl implements ChildrenDelegate {

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public TrainTreeNode getChildNode(TrainTreeNode node, Train train) {
        return null;
    }

    @Override
    public int addChildNode(TrainTreeNode trainTreeNode, TrainTreeNode newChildNode) {
        return 0;
    }
}
