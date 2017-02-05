package net.parostroj.timetable.model.freight;

import java.util.List;

/**
 * Freight connection between two nodes.
 *
 * @author jub
 */
public interface NodeFreightConnection extends NodeConnection {

    /**
     * @return list of steps
     */
    List<DirectNodeConnection> getSteps();

    /**
     * @return if the connection exist and is complete
     */
    boolean isComplete();
}