package net.parostroj.timetable.gui.views.tree;

import java.util.Collection;
import java.util.Collections;

import javax.swing.tree.TreePath;

import net.parostroj.timetable.model.Train;

/**
 * Delegate for leaf train node.
 *
 * @author jub
 */
class TrainDelegateImpl implements TrainTreeNodeDelegate<Train> {

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public Collection<Train> getTrains(TrainTreeNode<Train> node) {
        return Collections.singleton(node.getItem());
    }

    @Override
    public TreePath addTrain(TrainTreeNode<Train> node, Train train) {
        // cannot add to leaf
        return null;
    }

    @Override
    public TreePath removeTrain(TrainTreeNode<Train> node, Train train) {
        return train == node.getItem() ? new TreePath(node) : null;
    }

    @Override
    public TreePath getTrainPath(TrainTreeNode<Train> node, Train train) {
        return train == node.getItem() ? new TreePath(node) : null;
    }

    @Override
    public String getNodeText(Train item) {
        return item.getName();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public void sortChildren(TrainTreeNode<Train> node) {
        // nothing - it's leaf
    }
}
