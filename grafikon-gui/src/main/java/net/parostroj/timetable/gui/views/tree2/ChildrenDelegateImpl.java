package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.Train;

/**
 * Abstract delegate - common parts.
 *
 * @author jub
 */
public abstract class ChildrenDelegateImpl implements ChildrenDelegate {

    @Override
    public TrainTreeNode getChildNode(TrainTreeNode node, Train train) {
        for (TrainTreeNode childNode : TrainTreeHandler.getIterable(node.children(), TrainTreeNode.class)) {
            if (childNode.isNode(train)) {
                return childNode;
            }
        }
        return null;
    }
}
