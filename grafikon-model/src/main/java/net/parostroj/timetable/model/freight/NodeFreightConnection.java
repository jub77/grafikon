package net.parostroj.timetable.model.freight;

import java.util.List;
import java.util.Set;

/**
 * Freight connection between two nodes.
 *
 * @author jub
 */
public interface NodeFreightConnection extends NodeConnection {

    /**
     * One step in connection path.
     */
    public interface Step extends NodeConnection {

        Set<List<TrainConnection>> getConnections();
    }

    /**
     * @return list of steps
     */
    List<Step> getSteps();

    /**
     * @return if the connection exist and is complete
     */
    boolean isComplete();
}