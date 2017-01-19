package net.parostroj.timetable.model.freight;

import java.util.List;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Freight connection containing information about trains which are to be taken to reach the destination.
 *
 * @author jub
 */
public interface FreightConnectionPath extends FreightConnection {

    TimeInterval getTimeInterval();

    List<TimeInterval> getPath();
}
