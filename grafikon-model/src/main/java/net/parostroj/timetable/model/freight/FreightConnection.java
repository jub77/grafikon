package net.parostroj.timetable.model.freight;

import net.parostroj.timetable.model.Node;

/**
 * Freight connection from node to freight destination.
 *
 * @author jub
 */
public interface FreightConnection extends Connection<Node, FreightDestination> {
}
