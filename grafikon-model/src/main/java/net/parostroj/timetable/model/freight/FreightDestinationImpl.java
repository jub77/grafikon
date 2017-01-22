package net.parostroj.timetable.model.freight;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final boolean nodeCenterAllowed;

    public FreightDestinationImpl(Set<Region> regions) {
        this(null, true, regions);
    }

    public FreightDestinationImpl(Node node, boolean nodeCenterAllowed) {
        this(node, true, null);
    }

    private FreightDestinationImpl(Node node, boolean nodeCenterAllowed, Set<Region> regions) {
        this.node = node;
        this.regions = regions;
        this.nodeCenterAllowed = nodeCenterAllowed;
    }

    @Override
    public Set<Region> getRegions() {
        return regions != null ? regions
                : ((nodeCenterAllowed && node != null) ? node.getCenterRegions() : Collections.emptySet());
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public Set<FreightColor> getFreightColors() {
        Set<FreightColor> colors = node != null ? node.getFreightColors() : Collections.emptySet();
        Set<Region> centerRegions = getRegions();
        if (!centerRegions.isEmpty()) {
            colors = Stream.concat(colors.stream(), centerRegions.stream()
                    .flatMap(r -> r.getAllNodes().stream().flatMap(n -> n.getFreightColors().stream())).distinct())
                    .collect(Collectors.toSet());
        }
        return colors;
    }

    @Override
    public boolean isNode() {
        return node != null;
    }

    @Override
    public boolean isRegions() {
        return regions != null || (nodeCenterAllowed && node != null && node.isCenterOfRegions());
    }

    @Override
    public boolean isFreightColors() {
        return !getFreightColors().isEmpty();
    }

    @Override
    public String toString() {
        return node.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, regions);
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
        return Objects.equals(node, other.node) && Objects.equals(regions, other.regions);
    }
}
