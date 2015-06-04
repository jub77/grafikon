package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TrainsCycle;

/**
 * Delegate for circulations.
 *
 * @author jub
 */
public class TrainsCycleWrapperDelegate extends BasicWrapperDelegate<TrainsCycle> {

    private final boolean showType;

    public TrainsCycleWrapperDelegate() {
        this(false);
    }

    public TrainsCycleWrapperDelegate(boolean showType) {
        this.showType = showType;
    }

    @Override
    protected String toCompareString(TrainsCycle element) {
        return element.getName();
    }

    @Override
    public String toString(TrainsCycle element) {
        TrainsCycle cycle = element;
        String str = cycle.getName();
        if (showType) {
            str = String.format("%s (%s)", cycle.getName(), cycle.getType().getDescriptionText());
        }
        return str;
    }
}
