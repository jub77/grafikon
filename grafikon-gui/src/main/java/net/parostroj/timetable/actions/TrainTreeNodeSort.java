/*
 * NodeSort.java
 * 
 * Created on 8.9.2007, 14:44:24
 */
package net.parostroj.timetable.actions;

import java.util.*;
import net.parostroj.timetable.gui.views.TrainTreeNodeTrain;

/**
 * Sorting of nodes.
 * 
 * @author jub
 */
public class TrainTreeNodeSort {
    
    private Comparator<TrainTreeNodeTrain> comparator;
    
    public TrainTreeNodeSort(final TrainComparator trainComparator) {
        comparator = new Comparator<TrainTreeNodeTrain>() {
            @Override
            public int compare(TrainTreeNodeTrain o1, TrainTreeNodeTrain o2) {
                return trainComparator.compare(o1.getTrain(), o2.getTrain());
            }
        };
    }
    
    /**
     * sorts list of trains.
     * 
     * @param trainNodes trains
     * @return sorted list
     */
    public List<TrainTreeNodeTrain> sort(Collection<TrainTreeNodeTrain> trainNodes) {
        List<TrainTreeNodeTrain> newTrainNodes = new ArrayList<TrainTreeNodeTrain>(trainNodes);
        Collections.sort(newTrainNodes, comparator);
        return newTrainNodes;
    }
}