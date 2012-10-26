package net.parostroj.timetable.gui.views.tree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.model.Train;

/**
 * Common category delegate.
 *
 * @author jub
 */
public abstract class CategoryDelegateImpl<T> implements TrainTreeNodeDelegate<T> {

    private final boolean containTrains;
    private final TrainTreeNodeSort sort;
    private final Filter<Train> filter;

    public CategoryDelegateImpl(boolean containTrains, TrainTreeNodeSort sort, Filter<Train> filter) {
        this.containTrains = containTrains;
        this.sort = sort;
        this.filter = filter;
    }

    public CategoryDelegateImpl(boolean containTrains, TrainTreeNodeSort sort) {
        this(containTrains, sort, null);
    }

    public CategoryDelegateImpl(boolean containsTrains) {
        this(containsTrains, null, null);
    }

    @Override
    public TreePath addTrain(TrainTreeNode<T> node, Train train) {
        if (belongs(train, node.getItem()) && ((filter != null && filter.is(train)) || filter == null)) {
            if (containTrains) {
                TrainTreeNode<Train> trainNode = TrainTreeNodeFactory.getInstance().createTrainNode(node, train);
                // check if the train is not already there
                for (TrainTreeNode<?> item : node.getChildren()) {
                    if (item.getItem() == train)
                        return null;
                }
                node.getChildren().add(trainNode);
                this.sortChildren(node);
                return new TreePath(node).pathByAddingChild(trainNode);
            } else {
                for (TrainTreeNode<?> child : node.getChildren()) {
                    TreePath path = child.addTrain(train);
                    if (path != null)
                        return addToPath(node, path);
                }
            }
        }
        return null;
    }

    @Override
    public Collection<Train> getTrains(TrainTreeNode<T> node) {
        List<Train> result = new LinkedList<Train>();
        for (TrainTreeNode<?> child : node.getChildren()) {
            result.addAll(child.getTrains());
        }
        return result;
    }

    @Override
    public TreePath getTrainPath(TrainTreeNode<T> node, Train train) {
        for (TrainTreeNode<?> child : node.getChildren()) {
            TreePath path = child.getTrainPath(train);
            if (path != null)
                return addToPath(node, path);
        }
        return null;
    }

    @Override
    public TreePath removeTrain(TrainTreeNode<T> node, Train train) {
        if (containTrains) {
            for (TrainTreeNode<?> child : node.getChildren()) {
                TreePath path = child.removeTrain(train);
                if (path != null) {
                    node.getChildren().remove(child);
                    return addToPath(node, path);
                }
            }
        } else {
            for (TrainTreeNode<?> child : node.getChildren()) {
                TreePath path = child.removeTrain(train);
                if (path != null)
                    return addToPath(node, path);
            }
        }
        return null;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public void sortChildren(TrainTreeNode<T> node) {
        if (sort != null)
            sort.sort(node.getChildren());
    }

    protected abstract boolean belongs(Train train, T item);

    private TreePath addToPath(TreeNode node, TreePath path) {
        Object[] rest = path.getPath();
        Object[] all = new Object[rest.length + 1];
        all[0] = node;
        System.arraycopy(rest, 0, all, 1, rest.length);
        return new TreePath(all);
    }
}
