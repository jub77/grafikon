package net.parostroj.timetable.actions;

import java.util.Collection;
import java.util.List;

import net.parostroj.timetable.model.TrainsCycle;

/**
 * TrainsCycle sort - for output compatibility. It uses internally {@link ElementSort}.
 *
 * @author jub
 */
public class TrainsCycleSort {

    public enum Type {
        ASC
    };

    public TrainsCycleSort(Type type) {
    }

    public List<TrainsCycle> sort(Collection<TrainsCycle> nodes) {
        return new ElementSort<TrainsCycle>(new TrainsCycleComparator()).sort(nodes);
    }
}
