package net.parostroj.timetable.model.freight;

import java.util.Map;
import java.util.Set;

import net.parostroj.timetable.model.Node;

/**
 * Information about freight transportation.
 *
 * @author jub
 */
public interface NodeFreight {

    /**
     * @return set of connections with the destination of nodes
     */
    Set<FreightConnectionVia> getDirectConnections();

    /**
     * @return set of connections with the destination of regions
     */
    Set<FreightConnectionVia> getRegionConnections();

    /**
     * @return filtered out connections (direct and region) with freight color as destination
     */
    Set<FreightConnectionVia> getFreightColorConnections();

    /**
     * @return view on direct connections grouped by destination node
     */
    Map<Node, FreightConnectionVia> getDirectConnectionsMap();
}
