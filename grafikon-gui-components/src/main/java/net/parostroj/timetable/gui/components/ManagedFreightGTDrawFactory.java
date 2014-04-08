package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.filters.ManagedFreightTrainFilter;
import net.parostroj.timetable.gui.components.ManagedFreightGTDraw.Highlight;
import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TimeInterval;

public class ManagedFreightGTDrawFactory extends NormalGTDrawFactory {

    private final Highlight highlight;

    public ManagedFreightGTDrawFactory(Highlight highlight) {
        this.highlight = highlight;
    }

    @Override
    public GTDraw createInstance(GTViewSettings settings, Route route, GTStorage storage) {
        // replace filter ...
        storage.setFilter(TimeInterval.class, new Filter<TimeInterval>() {
            private final ManagedFreightTrainFilter trainFilter = new ManagedFreightTrainFilter();

            @Override
            public boolean is(TimeInterval item) {
                return trainFilter.is(item.getTrain());
            }
        });
        GTDraw draw = super.createInstance(settings, route, storage);
        // decorate
        return new ManagedFreightGTDraw(draw, storage.getCollector(FNConnection.class), highlight);
    }

}
