package net.parostroj.timetable.gui.views.tree2;

import net.parostroj.timetable.model.Train;

/**
 * Node with train.
 *
 * @author jub
 */
public class NodeDelegateTrainImpl implements NodeDelegate {

    @Override
    public String getNodeText(TrainTreeNode trainTreeNode) {
        return ((Train) trainTreeNode.getUserObject()).getName();
    }

    @Override
    public Object getUserObject(Train train) {
        return train;
    }

    @Override
    public boolean isNode(TrainTreeNode trainTreeNode, Train train) {
        return trainTreeNode.getUserObject() == train;
    }
}
