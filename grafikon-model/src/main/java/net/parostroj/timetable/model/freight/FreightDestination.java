package net.parostroj.timetable.model.freight;

import java.util.Set;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;

/**
 * Freight destination (node or broader - regions).
 *
 * @author jub
 */
public interface FreightDestination {

    Set<Region> getRegions();

    Node getNode();

    Set<FreightColor> getFreightColors();

    boolean isRegions();

    boolean isNode();

    boolean isFreightColors();

    default boolean isVisible() {
        return !isNode() || !getNode().getType().isHidden();
    }
}
