package net.parostroj.timetable.output2.gt;

import java.util.function.Predicate;
import net.parostroj.timetable.filters.ModelPredicates;
import net.parostroj.timetable.model.*;

public class ManagedFreightGTDrawFactory extends NormalGTDrawFactory {

    @Override
    public GTDraw createInstance(GTDraw.Type type, GTDrawSettings settings, Route route, GTStorage storage) {
        // replace filter ...
        final Predicate<Train> trainPredicate = ModelPredicates.managedTrain();
        storage.setFilter(TimeInterval.class, item -> trainPredicate.test(item.getTrain()));
        GTDraw draw = super.createInstance(type, settings, route, storage);
        // decorate
        return new ManagedFreightGTDraw(settings, draw, storage);
    }
}
