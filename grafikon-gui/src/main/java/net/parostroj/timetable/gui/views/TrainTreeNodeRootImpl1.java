/*
 * TrainTreeNodeRootImpl1.java
 *
 * Created on 24.8.2007, 12:59:23
 */
package net.parostroj.timetable.gui.views;

import java.util.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;

/**
 * Root node for tree of trains.
 * 
 * @author jub
 */
public class TrainTreeNodeRootImpl1 implements TrainTreeNodeRoot {

    private List<TrainTreeNodeType> children;

    private String text;

    public TrainTreeNodeRootImpl1(String text, TrainDiagram diagram) {
        this.children = new LinkedList<TrainTreeNodeType>();
        for (TrainType trainType : diagram.getTrainTypes()) {
            TrainTreeNodeType typeNode = new TrainTreeNodeType(diagram, trainType, this);
            children.add(typeNode);
        }
        this.text = text;
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
        for (TrainTreeNodeType nodeType : children) {
            TreePath addPath = nodeType.addTrain(path,train);
            if (addPath != null) {
                path = addPath;
                break;
            }
        }
        return path;
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
        for (TrainTreeNodeType nodeType : children) {
            TreePath removePath = nodeType.removeTrain(path,train);
            if (removePath != null) {
                path = removePath;
                break;
            }
        }
        return path;
    }
    
    @Override
    public TreePath getTrainPath(Train train) {
        TreePath path = new TreePath(this);
        TreePath result = null;
        for (TrainTreeNodeType nodeType : children) {
            TreePath selectedPath = nodeType.getTrainPath(path,train);
            if (selectedPath != null) {
                result = selectedPath;
                break;
            }
        }
        return result;
    }

    @Override
    public Set<Train> getTrains(TrainDiagram diagram) {
        return new HashSet<Train>(diagram.getTrains());
    }
}
