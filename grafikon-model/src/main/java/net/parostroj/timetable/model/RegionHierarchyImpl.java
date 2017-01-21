package net.parostroj.timetable.model;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class RegionHierarchyImpl implements RegionHierarchy {

    @Override
    public Map<FreightColor, Region> getFreightColorMap() {
        return this
                .find(r -> r.isColorCenter())
                .map(r -> r.getFreightColorMap())
                .orElse(Collections.emptyMap());
    }

    @Override
    public Region getColorCenter() {
        return this
                .find(r -> r.isColorCenter())
                .orElse(null);
    }

    @Override
    public Region getFirstSuperRegion() {
        Set<Region> regions = getRegions();
        // all regions has to have the same super region
        return regions.isEmpty() ? null : regions.iterator().next().getSuperRegion();
    }

    @Override
    public Region getTopSuperRegion() {
        Region firstSuperRegion = getFirstSuperRegion();
        if (firstSuperRegion == null) {
            return null;
        } else {
            Region current = firstSuperRegion;
            while (current.getSuperRegion() != null) {
                current = current.getSuperRegion();
            }
            return current;
        }
    }

    @Override
    public Optional<Region> find(Predicate<Region> predicate) {
        Optional<Region> result = getRegions().stream().filter(predicate).findAny();
        if (result.isPresent()) {
            return result;
        } else {
            return findInSuperRegions(predicate);
        }
    }

    @Override
    public Optional<Region> findInSuperRegions(Predicate<Region> predicate) {
        Region current = getFirstSuperRegion();
        while (current != null && !predicate.test(current)) {
            current = current.getSuperRegion();
        }
        return Optional.ofNullable(current);
    }

    @Override
    public void apply(Consumer<Region> consumer) {
        getRegions().stream().forEach(consumer);
        Region current = getFirstSuperRegion();
        while (current != null) {
            consumer.accept(current);
            current = current.getSuperRegion();
        }
    }
}
