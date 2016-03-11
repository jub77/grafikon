package net.parostroj.timetable.model;

import java.util.Collection;

/**
 * Connection between regions.
 *
 * @author jub
 */
public interface RegionConnection extends NodeConnection {
    Collection<TrainConnection> getConnections();
}
