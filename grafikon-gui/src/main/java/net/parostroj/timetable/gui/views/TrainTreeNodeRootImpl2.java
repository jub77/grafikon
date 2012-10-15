/*
 * TrainTreeNodeRootImpl2.java
 *
 * Created on 23.4.2008, 13:45:12
 */
package net.parostroj.timetable.gui.views;

import java.util.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainTreeNodeSort;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Root node for tree of trains (flat list).
 * 
 * @author jub
 */
public class TrainTreeNodeRootImpl2 implements TrainTreeNodeRoot {

    private List<TrainTreeNodeTrain> children;

    private String text;

    private TrainTreeNodeSort sort;

    public TrainTreeNodeRootImpl2(String text, TrainDiagram diagram) {
        this.sort = new TrainTreeNodeSort(new TrainComparator(TrainComparator.Type.ASC, diagram.getTrainsData().getTrainSortPattern()));
        this.children = new ArrayList<TrainTreeNodeTrain>();
        this.text = text;
        for (Train train : diagram.getTrains()) {
            TrainTreeNodeTrain nodeTrain = new TrainTreeNodeTrain(train, this);
            children.add(nodeTrain);
        }
        children = sort.sort(children);
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
    public TreeNode getParent() {
        return null;
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
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
    public Enumeration<?> children() {
        return Collections.enumeration(children);
    }

    @Override
    public String toString() {
        return text;
    }

    /**
     * adds new train to the tree and returns its path.
     * 
     * @param train train to be added
     * @return path
     */
    @Override
    public TreePath addTrain(Train train) {
        TreePath path = new TreePath(this);
        // add train
        TrainTreeNodeTrain newNode = new TrainTreeNodeTrain(train, this);
        children.add(newNode);
        children = sort.sort(children);
        return path.pathByAddingChild(newNode);
    }
    
    /**
     * removes train from the tree and returns its former path.
     * 
     * @param train train to be removed
     * @return path
     */
    @Override
    public TreePath removeTrain(Train train) {
        TreePath path = new TreePath(this);
        for (TrainTreeNodeTrain trainNode : children) {
            if (trainNode.getTrain() == train) {
                children.remove(trainNode);
                children = sort.sort(children);
                return path.pathByAddingChild(trainNode);
            }
        }
        return null;
    }
    
    @Override
    public TreePath getTrainPath(Train train) {
        TreePath path = new TreePath(this);
        for (TrainTreeNodeTrain trainNode : children) {
            if (trainNode.getTrain() == train) {
                return path.pathByAddingChild(trainNode);
            }
        }
        return null;
    }

    @Override
    public Set<Train> getTrains(TrainDiagram diagram) {
        return new HashSet<Train>(diagram.getTrains());
    }
}
