package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.filters.ModelPredicates;
import net.parostroj.timetable.gui.components.ManagedFreightGTDraw.Highlight;
import net.parostroj.timetable.model.*;

import com.google.common.base.Predicate;

public class ManagedFreightGTDrawFactory extends NormalGTDrawFactory {

    private final Highlight highlight;

    public ManagedFreightGTDrawFactory(Highlight highlight) {
        this.highlight = highlight;
    }

    @Override
    public GTDraw createInstance(GTViewSettings.Type type, GTDrawSettings settings, Route route, GTStorage storage) {
        // replace filter ...
        storage.setFilter(TimeInterval.class, new Predicate<TimeInterval>() {
            private final Predicate<Train> trainPredicate = ModelPredicates.managedTrain();

            @Override
            public boolean apply(TimeInterval item) {
                return trainPredicate.apply(item.getTrain());
            }
        });
        GTDraw draw = super.createInstance(type, settings, route, storage);
        // decorate
        return new ManagedFreightGTDraw(settings, draw, storage.getCollector(FNConnection.class), highlight);
    }

}
