package net.parostroj.timetable.model.freight;

import java.util.Objects;
import java.util.Set;

import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.freight.FreightConnectionImpl.RegionsRegionHierarchy;

class FreightDestinationFromWrapper implements FreightDestination {

    private Node from;
    private FreightDestination destination;

    FreightDestinationFromWrapper(Node from, FreightDestination destination) {
        this.from = from;
        this.destination = destination;
    }

    public Node getFrom() {
        return from;
    }

    public FreightDestination getDestination() {
        return destination;
    }

    @Override
    public Set<Region> getRegions() {
        return FreightAnalyser.transformToRegions(
                from.getRegionHierarchy(),
                new RegionsRegionHierarchy(destination.getRegions()));
    }

    @Override
    public Node getNode() {
        return destination.getNode();
    }

    @Override
    public Set<FreightColor> getFreightColors() {
        // TODO implementation
        return destination.getFreightColors();
    }

    @Override
    public boolean isRegions() {
        return destination.isRegions();
    }

    @Override
    public boolean isNode() {
        return destination.isNode();
    }

    @Override
    public boolean isFreightColors() {
        return !getFreightColors().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s:%s", from, destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, destination);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FreightDestinationFromWrapper other = (FreightDestinationFromWrapper) obj;
        return Objects.equals(from, other.from) && Objects.equals(destination, other.destination);
    }
}
