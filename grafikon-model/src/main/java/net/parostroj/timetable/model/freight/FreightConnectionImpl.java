package net.parostroj.timetable.model.freight;

import java.util.Objects;
import java.util.Set;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.RegionHierarchyImpl;

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
        return String.format("%s to %s", from, to);
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

    static class RegionsRegionHierarchy extends RegionHierarchyImpl {

        private final Set<Region> regions;

        public RegionsRegionHierarchy(Set<Region> regions) {
            this.regions = regions;
        }

        @Override
        public Set<Region> getRegions() {
            return regions;
        }
    }
}
