package net.parostroj.timetable.model.freight;

import java.util.Set;

import net.parostroj.timetable.model.Node;

/**
 * Information about freight transportation.
 *
 * @author jub
 */
public interface NodeFreight {

    Node getFrom();

    Set<FreightConnectionVia> getConnections();

    /**
     * @return set of connections with the destination of nodes
     */
    Set<FreightConnectionVia> getNodeConnections();

    /**
     * @return set of connections with the destination of regions
     */
    Set<FreightConnectionVia> getRegionConnections();

    /**
     * @return filtered out connections (direct and region) with freight color as destination
     */
    Set<FreightConnectionVia> getFreightColorConnections();
}
