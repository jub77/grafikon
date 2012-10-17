/*
 * TrainTreeNode.java
 *
 * Created on 24.8.2007, 12:40:57
 */
package net.parostroj.timetable.gui.views;

import java.util.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainTreeNodeSort;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.Train;

/**
 *  Node for JTree with type.
 *
 * @author jub
 */
public class TrainTreeNodeType implements TrainTreeNode {

    private final List<TrainTreeNodeTrain> children;
    private final TrainType trainType;
    private final TreeNode parent;
    private final TrainTreeNodeSort sort;

    public TrainTreeNodeType(TrainDiagram diagram, TrainType trainType, TreeNode parent) {
        this.sort = new TrainTreeNodeSort(new TrainComparator(TrainComparator.Type.ASC, diagram.getTrainsData().getTrainSortPattern()));
        this.trainType = trainType;
        this.children = new LinkedList<TrainTreeNodeTrain>();
        this.parent = parent;

        for (Train train : diagram.getTrains()) {
            if (train.getType() == trainType) {
                TrainTreeNodeTrain nodeTrain = new TrainTreeNodeTrain(train, this);
                children.add(nodeTrain);
            }
        }
        sort.sortInPlace(children);
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
        return parent;
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

    public TreePath addTrain(TreePath path, Train train) {
        if (trainType != train.getType())
            return null;
        // add train
        TrainTreeNodeTrain newNode = new TrainTreeNodeTrain(train, this);
        children.add(newNode);
        sort.sortInPlace(children);
        return path.pathByAddingChild(this).pathByAddingChild(newNode);
    }

    public TreePath removeTrain(TreePath path, Train train) {
        // remove train
        TrainTreeNodeTrain found = null;
        for (TrainTreeNodeTrain trainNode : children) {
            if (trainNode.getTrain() == train) {
                found = trainNode;
                break;
            }
        }
        if (found != null) {
            children.remove(found);
            sort.sortInPlace(children);
            return path.pathByAddingChild(this).pathByAddingChild(found);
        } else
            return null;
    }

    public TreePath getTrainPath(TreePath path, Train train) {
        // check train
        TrainTreeNodeTrain found = null;
        for (TrainTreeNodeTrain trainNode : children) {
            if (trainNode.getTrain() == train) {
                found = trainNode;
                break;
            }
        }
        if (found != null) {
            return path.pathByAddingChild(this).pathByAddingChild(found);
        } else
            return null;
    }

    @Override
    public String toString() {
        return trainType.getDesc();
    }

    @Override
    public Set<Train> getTrains(TrainDiagram diagram) {
        // filter by train type
        Set<Train> trainSet = new HashSet<Train>();
        for (TrainTreeNodeTrain node : children) {
            trainSet.add(node.getTrain());
        }
        return trainSet;
    }
}
