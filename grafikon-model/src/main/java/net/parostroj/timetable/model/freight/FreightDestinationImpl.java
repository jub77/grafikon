package net.parostroj.timetable.model.freight;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;

/**
 * Destination implementation.
 *
 * @author jub
 */
class FreightDestinationImpl implements FreightDestination {

    private final Node node;
    private final Set<Region> regions;
    private final Set<FreightColor> freightColors;

    public FreightDestinationImpl(Node node, Set<Region> regions, Set<FreightColor> freightColors) {
        this.node = node;
        this.regions = regions;
        this.freightColors = freightColors;
    }

    @Override
    public Set<Region> getRegions() {
        return regions == null ? Collections.emptySet() : regions;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Set<FreightColor> getFreightColors() {
        return freightColors == null ? Collections.emptySet() : freightColors;
    }

    @Override
    public boolean isNode() {
        return node != null;
    }

    @Override
    public boolean isRegions() {
        return !getRegions().isEmpty();
    }

    @Override
    public boolean isFreightColors() {
        return !getFreightColors().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("<%s,%s,%s>", node == null ? "-" : node, regions, freightColors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, regions, freightColors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FreightDestinationImpl other = (FreightDestinationImpl) obj;
        return Objects.equals(node, other.node) && Objects.equals(this.getRegions(), other.getRegions())
                && Objects.equals(this.getFreightColors(), other.getFreightColors());
    }
}
