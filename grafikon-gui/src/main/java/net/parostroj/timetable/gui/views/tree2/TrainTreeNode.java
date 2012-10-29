package net.parostroj.timetable.gui.views.tree2;

import javax.swing.tree.DefaultMutableTreeNode;

import net.parostroj.timetable.model.Train;

/**
 * Train tree node.
 *
 * @author jub
 */
public class TrainTreeNode extends DefaultMutableTreeNode {

    private final NodeDelegate nodeDelegate;
    private final ChildrenDelegate childrenDelegate;

    public TrainTreeNode(Train train, NodeDelegate nodeDelegate, ChildrenDelegate childrenDelegate) {
        super(nodeDelegate.getUserObject(train));
        this.nodeDelegate = nodeDelegate;
        this.childrenDelegate = childrenDelegate;
    }

    @Override
    public boolean isLeaf() {
        return childrenDelegate.isLeaf();
    }

    @Override
    public boolean getAllowsChildren() {
        return childrenDelegate.getAllowsChildren();
    }

    @Override
    public String toString() {
        return nodeDelegate.getNodeText(this);
    }

    public TrainTreeNode getChildNode(Train train) {
        return childrenDelegate.getChildNode(this, train);
    }

    public boolean isNode(Train train) {
        return nodeDelegate.isNode(this, train);
    }

    public int addChildNode(TrainTreeNode newChildNode) {
        return childrenDelegate.addChildNode(this, newChildNode);
    }
}
