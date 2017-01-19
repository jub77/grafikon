package net.parostroj.timetable.model.freight;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;

class FreightConnectionImpl implements FreightConnection {

    private final Node from;
    private final FreightDestination to;

    public FreightConnectionImpl(Node from, FreightDestination to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Node getFrom() {
        return from;
    }

    @Override
    public FreightDestination getTo() {
        return to;
    }

    @Override
    public String toString() {
        return to.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FreightConnectionImpl other = (FreightConnectionImpl) obj;
        return Objects.equals(this.from, other.from) && Objects.equals(this.to, other.to);
    }

    @Override
    public Set<Region> getToRegions() {
        if (!this.getTo().isCenter()) return Collections.emptySet();
        Set<Region> toCenterRegions = this.getTo().getRegions();
        Set<Region> fromRegions = getFrom().getRegions();
        return FreightAnalyser.transformToRegions(fromRegions, toCenterRegions);
    }

    @Override
    public Set<FreightColor> getToFreightColors() {
        Set<Region> regions = getTo().getRegions();

        Region commonRegion = FreightAnalyser.getFirstCommonRegion(getFrom().getRegions(), regions);
        Set<FreightColor> filtered = null;
        if (commonRegion == null) {
            filtered = Optional.ofNullable(FreightAnalyser.getSuperRegion(regions))
                    .map(r -> r.getTopSuperRegion().getAllNodes().stream()
                            .flatMap(node -> node.getFreightColors().stream())
                            .collect(Collectors.toSet()))
                    .orElse(Collections.emptySet());
        } else {
            // common region - no filtering
            filtered = Collections.emptySet();
        }

        // get colors of the target region
        if (!regions.isEmpty()) {

        }


        // get all

        Set<Region> allRegionSet = regions.stream().flatMap(r -> r.getRegionHierarchy().stream()).distinct().collect(Collectors.toSet());
        Map<FreightColor, Region> filteredMap = getFrom().getRecursiveFreightColorMap().entrySet().stream().filter(e -> !allRegionSet.contains(e.getValue())).collect(Collectors.toMap(c -> c.getKey(), c -> c.getValue()));

        return getTo().isNode() ? getTo().getNode().getFreightColors() : Collections.emptySet();
    }
}
