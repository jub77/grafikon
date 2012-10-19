/*
 * NodeSort.java
 *
 * Created on 8.9.2007, 14:44:24
 */
package net.parostroj.timetable.gui.views.tree;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.model.Train;

/**
 * Sorting of nodes.
 *
 * @author jub
 */
public class TrainTreeNodeSortImpl implements TrainTreeNodeSort {

    private Comparator<TrainTreeNode<?>> comparator;

    public TrainTreeNodeSortImpl(final TrainComparator trainComparator) {
        comparator = new Comparator<TrainTreeNode<?>>() {
            @Override
            public int compare(TrainTreeNode<?> o1, TrainTreeNode<?> o2) {
                return trainComparator.compare((Train) o1.getItem(), (Train) o2.getItem());
            }
        };
    }

    /**
     * sorts list in place.
     *
     * @param trainNodes trains
     */
    @Override
    public void sort(List<TrainTreeNode<?>> trainNodes) {
        Collections.sort(trainNodes, comparator);
    }
}