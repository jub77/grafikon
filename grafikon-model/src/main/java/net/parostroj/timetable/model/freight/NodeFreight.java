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

    Set<FreightConnectionVia> getDirectConnections();

    Set<FreightConnectionVia> getRegionConnections();

    Map<Node, FreightConnectionVia> getDirectConnectionsMap();
}
