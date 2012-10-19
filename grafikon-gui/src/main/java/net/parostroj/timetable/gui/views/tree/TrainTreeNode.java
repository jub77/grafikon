package net.parostroj.timetable.gui.views.tree;

import java.util.Collection;
import java.util.List;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.parostroj.timetable.model.Train;

/**
 * Common interface for all nodes.
 *
 * @author jub
 */
public interface TrainTreeNode<T> extends TreeNode {

    /**
     * returns set of trains under the node.
     *
     * @param diagram diagram
     * @return set of trains
     */
    public Collection<Train> getTrains();

    /**
     * adds new train to the tree and returns its path.
     *
     * @param train train to be added
     * @return path
     */
    public TreePath addTrain(Train train);

    /**
     * removes train from the tree and returns its former path.
     *
     * @param train train to be removed
     * @return path
     */
    public TreePath removeTrain(Train train);

    /**
     * returns path of the train within the tree.
     *
     * @param train train
     * @return path
     */
    public TreePath getTrainPath(Train train);

    /**
     * @return item
     */
    public T getItem();

    /**
     * sorts children.
     */
    public void sortChildren();

    /**
     * @return children of the node
     */
    public List<TrainTreeNode<?>> getChildren();

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getParent()
     */
    @Override
    public TrainTreeNode<?> getParent();
}
