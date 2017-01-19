package net.parostroj.timetable.model.freight;

import java.util.Set;

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

    default boolean isNode() {
        return getNode() != null;
    }

    default boolean isCenter() {
        Set<Region> regions = getRegions();
        return regions != null && !regions.isEmpty();
    }

    default boolean isVisible() {
        return isNode() ? !getNode().getType().isHidden() : true;
    }
}
