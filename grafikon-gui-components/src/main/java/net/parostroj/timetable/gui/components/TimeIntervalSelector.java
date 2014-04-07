package net.parostroj.timetable.gui.components;

import java.util.List;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Selects time intervals after clicking in the graphical timetable view.
 *
 * @author jub
 */
public interface TimeIntervalSelector extends RegionSelector<TimeInterval> {
    /**
     * callback for selected intervals.
     *
     * @param intervals train intervals to be selected
     */
    public void regionsSelected(List<TimeInterval> intervals);

    /**
     * edit selected (double click).
     */
    public void editSelected();
}
