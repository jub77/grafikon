package net.parostroj.timetable.model;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Wrapper for hierarchy of regions starting with set of regions at the bottom.
 *
 * @author jub
 */
public interface RegionHierarchy {

    Set<Region> getRegions();

    Map<FreightColor, Region> getFreightColorMap();

    Region getFreightColorRegion();

    Region getFirstSuperRegion();

    Region getTopSuperRegion();

    Optional<Region> find(Predicate<Region> predicate);

    void apply(Consumer<Region> consumer);

    Optional<Region> findInSuperRegions(Predicate<Region> predicate);
}
