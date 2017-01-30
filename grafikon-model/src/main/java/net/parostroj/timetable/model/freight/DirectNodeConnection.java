package net.parostroj.timetable.model.freight;

import java.util.Collection;

/**
 * Connection between nodes containing collection of trains.
 *
 * @author jub
 */
public interface DirectNodeConnection extends NodeConnection {
    Collection<TrainConnection> getConnections();
}
