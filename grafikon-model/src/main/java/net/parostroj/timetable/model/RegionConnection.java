package net.parostroj.timetable.model;

import java.util.Collection;

import net.parostroj.timetable.utils.Tuple;

/**
 * Connection between regions.
 *
 * @author jub
 */
public interface RegionConnection {
    Node getTo();
    Node getFrom();
    Collection<Tuple<TimeInterval>> getConnections();
}
