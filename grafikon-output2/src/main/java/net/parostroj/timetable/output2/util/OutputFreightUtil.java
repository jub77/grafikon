package net.parostroj.timetable.output2.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;

/**
 * Utilities for output specific to dealing with freight.
 *
 * @author jub
 */
public class OutputFreightUtil {

    public Collection<Region> targetCenterRegions(Node from, Node to) {
        if (!to.isCenterOfRegions()) throw new IllegalArgumentException("No center of region: " + to);
        Set<Region> toCenterRegions = to.getCenterRegions();
        Region toSuper = toCenterRegions.isEmpty() ? null : getSuperRegion(toCenterRegions);
        Set<Region> fromCenterRegions = from.getRegions();
        Region fromSuper = fromCenterRegions.isEmpty() ? null : getSuperRegion(fromCenterRegions);
        // all center regions has to have the same super region (if exists)
        if (!toCenterRegions.isEmpty() && !fromCenterRegions.isEmpty()) {
            if (fromSuper == null && toSuper != null) {
                toCenterRegions = Collections.singleton(toSuper.getTopSuperRegion());
            } else if (toSuper != null && !toSuper.isOnPathIn(fromSuper)) {
                Region dest = toSuper;
                while (dest.getSuperRegion() != null && !dest.getSuperRegion().isOnPathIn(fromSuper)) {
                    dest = dest.getSuperRegion();
                }
                toCenterRegions = Collections.singleton(dest);
            }
        }
        System.out.printf("From: %s, To: %s, Result: %s%n", from, to, toCenterRegions);
        return toCenterRegions;
    }

    // returns super region - the assumption is that regions share the same super region
    private Region getSuperRegion(Collection<? extends Region> toCenterRegions) {
        return toCenterRegions.iterator().next().getSuperRegion();
    }



    public Map<FreightColor, Region> freightColorMap(Node node) {
        return node.getRegions().stream().flatMap(region -> region.getFreightColorMap().entrySet().stream())
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }
}
