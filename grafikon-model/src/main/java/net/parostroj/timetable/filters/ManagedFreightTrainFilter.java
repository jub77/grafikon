package net.parostroj.timetable.filters;

import net.parostroj.timetable.actions.FreightHelper;
import net.parostroj.timetable.model.Train;

/**
 * Filters only trains with managed freigth setting.
 *
 * @author jub
 */
public class ManagedFreightTrainFilter implements Filter<Train> {

    @Override
    public boolean is(Train train) {
        return FreightHelper.isManaged(train);
    }
}
