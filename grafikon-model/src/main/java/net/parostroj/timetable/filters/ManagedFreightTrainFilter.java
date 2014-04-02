package net.parostroj.timetable.filters;

import net.parostroj.timetable.model.Train;

/**
 * Filters only trains with managed freigth setting.
 *
 * @author jub
 */
public class ManagedFreightTrainFilter implements Filter<Train> {

    @Override
    public boolean is(Train train) {
        return train.getAttributes().getBool(Train.ATTR_MANAGED_FREIGHT);
    }
}
