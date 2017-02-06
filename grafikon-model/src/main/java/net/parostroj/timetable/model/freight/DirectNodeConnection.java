package net.parostroj.timetable.model.freight;

import java.util.Set;

/**
 * Connection between nodes containing collection of trains.
 *
 * @author jub
 */
public interface DirectNodeConnection extends NodeConnection {

    Set<TrainPath> getConnections();
}
