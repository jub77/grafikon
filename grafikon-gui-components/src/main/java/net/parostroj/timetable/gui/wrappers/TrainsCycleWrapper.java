package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TrainsCycle;

/**
 * Trains cycle wrapper.
 *
 * @author jub
 */
public class TrainsCycleWrapper extends Wrapper<TrainsCycle> {

    public TrainsCycleWrapper(TrainsCycle node) {
        super(node);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return toString(getElement());
    }

    public static String toString(TrainsCycle cycle) {
        return cycle.getName();
    }
}
