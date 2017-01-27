package net.parostroj.timetable.model.freight;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TimeInterval;

public class TransportImpl implements Transport {

    private final Set<Region> regions;
    private final Set<TimeInterval> trains;

    TransportImpl(Set<Region> regions, Set<TimeInterval> trains) {
        this.regions = regions;
        this.trains = trains;
    }

    @Override
    public Set<Region> getRegions() {
        return regions == null ? Collections.emptySet() : regions;
    }

    @Override
    public Set<TimeInterval> getTrains() {
        return trains == null ? Collections.emptySet() : trains;
    }

    @Override
    public Transport merge(Transport transport) {
        Set<Region> mergedRegions = new HashSet<>(getRegions());
        mergedRegions.addAll(transport.getRegions());
        Set<TimeInterval> mergedTrains = new HashSet<>(getTrains());
        mergedTrains.addAll(transport.getTrains());
        return new TransportImpl(mergedRegions, mergedTrains);
    }

    @Override
    public String toString() {
        return isDirect() ? trains.toString() : regions.toString();
    }
}