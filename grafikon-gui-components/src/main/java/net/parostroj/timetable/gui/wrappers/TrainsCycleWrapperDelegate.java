package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TrainsCycle;

/**
 * Delegate for circulations.
 *
 * @author jub
 */
public class TrainsCycleWrapperDelegate extends BasicWrapperDelegate {

    private final boolean showType;

    public TrainsCycleWrapperDelegate() {
        this(false);
    }

    public TrainsCycleWrapperDelegate(boolean showType) {
        this.showType = showType;
    }

    @Override
    protected String toCompareString(Object element) {
        return ((TrainsCycle) element).getName();
    }

    @Override
    public String toString(Object element) {
        TrainsCycle cycle = (TrainsCycle) element;
        String str = cycle.getName();
        if (showType) {
            str = String.format("%s (%s)", cycle.getName(), cycle.getType().getDescriptionText());
        }
        return str;
    }
}
