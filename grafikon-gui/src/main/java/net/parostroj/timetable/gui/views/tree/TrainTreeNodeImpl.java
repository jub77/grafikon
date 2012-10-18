package net.parostroj.timetable.gui.views.tree;

import java.util.*;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.parostroj.timetable.model.Train;

/**
 * Implementation of tree node.
 *
 * @author jub
 */
public class TrainTreeNodeImpl<T> implements TrainTreeNode<T> {

    private final TrainTreeNodeDelegate<T> delegate;
    private final T item;
    private final List<TrainTreeNode<?>> children;
    private final TrainTreeNode<?> parent;

    TrainTreeNodeImpl(TrainTreeNode<?> parent, TrainTreeNodeDelegate<T> delegate, T item) {
        this.parent = parent;
        this.delegate = delegate;
        this.item = item;
        this.children = new ArrayList<TrainTreeNode<?>>();
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TrainTreeNode<?> getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return delegate.getAllowsChildren();
    }

    @Override
    public boolean isLeaf() {
        return delegate.isLeaf();
    }

    @Override
    public Enumeration<?> children() {
        return Collections.enumeration(children);
    }

    @Override
    public Collection<Train> getTrains() {
        return delegate.getTrains(this);
    }

    @Override
    public TreePath addTrain(Train train) {
        return delegate.addTrain(this, train);
    }

    @Override
    public TreePath removeTrain(Train train) {
        return delegate.removeTrain(this, train);
    }

    @Override
    public TreePath getTrainPath(Train train) {
        return delegate.getTrainPath(this, train);
    }

    @Override
    public T getItem() {
        return item;
    }

    @Override
    public List<TrainTreeNode<?>> getChildren() {
        return children;
    }

    @Override
    public void sortChildren() {
        delegate.sortChildren(this);
    }

    @Override
    public String toString() {
        return delegate.getNodeText(item);
    }
}
