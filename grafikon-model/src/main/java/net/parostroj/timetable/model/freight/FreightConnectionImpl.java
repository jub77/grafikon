package net.parostroj.timetable.model.freight;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

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
        return FreightAnalyser.transformToRegions(getFrom().getRegionHierarchy(), getTo().getRegionHierarchy());
    }

    @Override
    public Set<FreightColor> getToFreightColors() {
        // TODO missing impl
        return null;
    }
}
