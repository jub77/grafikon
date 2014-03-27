package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TrainsCycle;

/**
 * Delegate for circulations.
 *
 * @author jub
 */
public class TrainsCycleWrapperDelegate implements WrapperDelegate {

    private final boolean showType;

    public TrainsCycleWrapperDelegate() {
        this(false);
    }

    public TrainsCycleWrapperDelegate(boolean showType) {
        this.showType = showType;
    }

    @Override
    public String toString(Object element) {
        TrainsCycle cycle = (TrainsCycle) element;
        String str = cycle.getName();
        if (showType) {
            str = String.format("%s (%s)", cycle.getName(), cycle.getType().getName());
        }
        return str;
    }

    @Override
    public int compare(Object o1, Object o2) {
        return ((TrainsCycle) o1).getName().compareTo(((TrainsCycle) o2).getName());
    }
}
