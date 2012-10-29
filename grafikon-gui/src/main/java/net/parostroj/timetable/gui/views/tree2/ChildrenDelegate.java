package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.Train;

/**
 * Children delegate.
 *
 * @author jub
 */
public interface ChildrenDelegate {

    boolean isLeaf();

    boolean getAllowsChildren();

    TrainTreeNode getChildNode(TrainTreeNode node, Train train);

    int addChildNode(TrainTreeNode trainTreeNode, TrainTreeNode newChildNode);
}
