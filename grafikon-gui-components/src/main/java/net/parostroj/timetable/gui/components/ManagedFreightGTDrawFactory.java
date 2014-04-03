package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.filters.ManagedFreightTrainFilter;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TimeInterval;

public class ManagedFreightGTDrawFactory extends NormalGTDrawFactory {

    @Override
    public GTDraw createInstance(GTViewSettings settings, Route route, TrainRegionCollector collector,
            Filter<TimeInterval> interval) {
        // replace filter ...
        GTDraw draw = super.createInstance(settings, route, collector, new Filter<TimeInterval>() {
            private final ManagedFreightTrainFilter trainFilter = new ManagedFreightTrainFilter();

            @Override
            public boolean is(TimeInterval item) {
                return trainFilter.is(item.getTrain());
            }
        });
        // decorate
        return new ManagedFreightGTDraw(draw);
    }

}
