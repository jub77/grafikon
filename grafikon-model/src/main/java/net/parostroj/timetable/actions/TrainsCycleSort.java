/*
 * NodeSort.java
 * 
 * Created on 8.9.2007, 14:44:24
 */
package net.parostroj.timetable.actions;

import java.text.Collator;
import java.util.*;
import net.parostroj.timetable.model.TrainsCycle;

/**
 * Sorting of nodes.
 * 
 * @author jub
 */
public class TrainsCycleSort {
    
    public enum Type {ASC, DESC; }
    
    private Type type;

    public TrainsCycleSort(Type type) {
        this.type = type;
    }
    
    /**
     * sorts list of engine cycles.
     * 
     * @param cycles engine cycles
     * @return sorted list
     */
    public List<TrainsCycle> sort(Collection<TrainsCycle> cycles) {
        Comparator<TrainsCycle> comparator = null;
        List<TrainsCycle> newCycles = new ArrayList<TrainsCycle>(cycles);
        switch (type) {
                                                                                                case ASC:
                comparator = new Comparator<TrainsCycle>() {
                    private Collator c = Collator.getInstance();
                    @Override
                    public int compare(TrainsCycle o1, TrainsCycle o2) {
                        return c.compare(o1.getName(), o2.getName());
                    }
                };
                break;
            case DESC:
                comparator = new Comparator<TrainsCycle>() {
                    private Collator c = Collator.getInstance();
                    @Override
                    public int compare(TrainsCycle o1, TrainsCycle o2) {
                        return c.compare(o2.getName(), o1.getName());
                    }
                };
                break;
        }
        Collections.sort(newCycles, comparator);
        return newCycles;
    }
}