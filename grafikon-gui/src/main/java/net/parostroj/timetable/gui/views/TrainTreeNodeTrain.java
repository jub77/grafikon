/*
 * TrainTreeNodeTrain.java
 *
 * Created on 24.8.2007, 12:41:56
 */
package net.parostroj.timetable.gui.views;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import javax.swing.tree.TreeNode;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Node for JTree with train in it.
 * 
 * @author jub
 */
public class TrainTreeNodeTrain implements TrainTreeNode {

    private Train train;

    private TreeNode parent;

    public TrainTreeNodeTrain(Train train, TreeNode parent) {
        this.train = train;
        this.parent = parent;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return 0;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration children() {
        return null;
    }

    public Train getTrain() {
        return train;
    }

    @Override
    public String toString() {
        return train.getName();
    }

    @Override
    public Set<Train> getTrains(TrainDiagram diagram) {
        return Collections.singleton(train);
    }
}
