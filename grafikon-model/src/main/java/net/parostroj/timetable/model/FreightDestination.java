package net.parostroj.timetable.model;

import java.util.Set;

import net.parostroj.timetable.model.freight.FreightAnalyser;
import net.parostroj.timetable.model.freight.NodeConnection;

/**
 * Destination for cargo.
 *
 * @author jub
 */
public interface FreightDestination extends NodeConnection {

    enum DestinationType {
        NODE, REGION
    }

    default DestinationType getDestinationType() {
        return DestinationType.NODE;
    }

    default Set<Region> getCenterRegions() {
        return getTo().getCenterRegions();
    }

    default Set<Region> getTargetRegionsFrom() {
        if (!this.isCenterOfRegions()) throw new IllegalArgumentException("No center of region: " + this);
        Set<Region> toCenterRegions = this.getCenterRegions();
        Node fromNode = getFrom();
        if (fromNode != null) {
            Set<Region> fromRegions = getFrom().getRegions();
            return FreightAnalyser.getTargetRegionsFrom(toCenterRegions, fromRegions);
        } else {
            return toCenterRegions;
        }
    }

    default boolean isCenterOfRegions() {
        return getTo().isCenterOfRegions();
    }

    default boolean isHidden() {
        return getTo().getType().isHidden();
    }

    default Set<FreightColor> getFreightColors() {
        return getTo().getFreightColors();
    }
}
