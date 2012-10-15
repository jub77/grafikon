/*
 * TrainTreeNodeRoot.java
 *
 * Created on 24.8.2007, 12:59:23
 */
package net.parostroj.timetable.gui.views;

import javax.swing.tree.TreePath;
import net.parostroj.timetable.model.Train;

/**
 * Root node for tree of trains.
 * 
 * @author jub
 */
public interface TrainTreeNodeRoot extends TrainTreeNode {

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

}
