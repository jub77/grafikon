package net.parostroj.timetable.model.freight;

import java.util.Set;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.RegionHierarchy;

/**
 * Freight destination (node or broader - regions).
 *
 * @author jub
 */
public interface FreightDestination {

    RegionHierarchy getRegionHierarchy();

    Node getNode();

    default boolean isNode() {
        return getNode() != null;
    }

    default boolean isCenter() {
        Set<Region> regions = getRegionHierarchy().getRegions();
        return regions != null && !regions.isEmpty();
    }

    default boolean isVisible() {
        return isNode() ? !getNode().getType().isHidden() : true;
    }
}
