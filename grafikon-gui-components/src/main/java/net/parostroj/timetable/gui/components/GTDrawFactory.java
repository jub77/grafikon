package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TimeInterval;

/**
 * GTDrawFactory for GTView.
 *
 * @author jub
 */
public abstract class GTDrawFactory {

    public abstract GTDraw createInstance(GTViewSettings settings, Route route, TrainRegionCollector collector, Filter<TimeInterval> interval);
}
