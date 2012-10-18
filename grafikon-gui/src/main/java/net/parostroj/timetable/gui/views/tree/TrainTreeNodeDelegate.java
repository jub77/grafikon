package net.parostroj.timetable.gui.views.tree;

import java.util.Collection;

import javax.swing.tree.TreePath;

import net.parostroj.timetable.model.Train;

/**
 * Delegate for TrainTreeNode.
 *
 * @author jub
 */
public interface TrainTreeNodeDelegate<T> {

    boolean getAllowsChildren();

    Collection<Train> getTrains(TrainTreeNode<T> node);

    TreePath addTrain(TrainTreeNode<T> node, Train train);

    TreePath removeTrain(TrainTreeNode<T> node, Train train);

    TreePath getTrainPath(TrainTreeNode<T> node, Train train);

    String getNodeText(T item);

    void sortChildren(TrainTreeNode<T> node);

    boolean isLeaf();
}
