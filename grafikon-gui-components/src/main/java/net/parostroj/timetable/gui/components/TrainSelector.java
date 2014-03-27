package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Selects train after clicking in the graphical timetable view.
 *
 * @author jub
 */
public interface TrainSelector {
    /**
     * selects train interval.
     *
     * @param interval train interval to be selected
     */
    public void selectTrainInterval(TimeInterval interval);

    /**
     * edit selected.
     */
    public void editSelected();

    /**
     * returns selected train interval.
     *
     * @return selected train interval
     */
    public TimeInterval getSelectedTrainInterval();
}
