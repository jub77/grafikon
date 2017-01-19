package net.parostroj.timetable.model.freight;

import java.util.Set;

import net.parostroj.timetable.model.TimeInterval;

/**
 * Transport for freight connection (via other connection or directly via exact train.
 *
 * @author jub
 */
public interface Transport {

    FreightConnection getConnection();

    Set<TimeInterval> getTrains();

    default boolean isDirect() {
        return getTrains() != null;
    }
}
