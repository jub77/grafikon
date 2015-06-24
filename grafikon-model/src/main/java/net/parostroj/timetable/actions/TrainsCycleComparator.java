package net.parostroj.timetable.actions;

import java.text.Collator;
import java.util.Comparator;

import net.parostroj.timetable.model.TrainsCycle;

/**
 * Comparator for circulations - it uses collator for name comparison.
 *
 * @author jub
 */
public class TrainsCycleComparator implements Comparator<TrainsCycle> {

    private final Collator collator = Collator.getInstance();

    @Override
    public int compare(TrainsCycle o1, TrainsCycle o2) {
        return collator.compare(o1.getName(), o2.getName());
    }
}
