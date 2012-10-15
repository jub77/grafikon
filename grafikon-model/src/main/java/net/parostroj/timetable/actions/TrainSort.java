/*
 * NodeSort.java
 * 
 * Created on 8.9.2007, 14:44:24
 */
package net.parostroj.timetable.actions;

import java.util.*;
import net.parostroj.timetable.model.Train;

/**
 * Sorting of nodes.
 * 
 * @author jub
 */
public class TrainSort {
    
    private TrainComparator comparator;

    public TrainSort(TrainComparator comparator) {
        this.comparator = comparator;
    }
    
    /**
     * sorts list of trains.
     * 
     * @param trains trains
     * @return sorted list
     */
    public List<Train> sort(Collection<Train> trains) {
        List<Train> newTrains = new ArrayList<Train>(trains);
        Collections.sort(newTrains, comparator);
        return newTrains;
    }
}