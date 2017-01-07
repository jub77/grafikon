package net.parostroj.timetable.model;

import java.util.List;

/**
 * Freight destination containing information about source and trains which are to be taken to reach the destination.
 *
 * @author jub
 */
public interface FreightDestinationWithPath extends FreightDestination {

    TimeInterval getTimeInterval();

    List<TimeInterval> getPath();
}
