package net.parostroj.timetable.model.freight;

import java.util.Set;

import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TimeInterval;

/**
 * Transport for freight connection (via other connection or directly via exact train).
 *
 * @author jub
 */
public interface Transport {

    Set<Region> getRegions();

    Set<TimeInterval> getTrains();

    Transport merge(Transport transport);

    default boolean isDirect() {
        return getTrains() != null && !getTrains().isEmpty();
    }
}
