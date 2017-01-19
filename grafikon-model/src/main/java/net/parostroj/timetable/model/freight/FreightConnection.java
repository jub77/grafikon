package net.parostroj.timetable.model.freight;

import java.util.Set;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;

/**
 * Freight connection from node to freight destination.
 *
 * @author jub
 */
public interface FreightConnection extends Connection<Node, FreightDestination> {

    Set<Region> getToRegions();

    Set<FreightColor> getToFreightColors();
}
