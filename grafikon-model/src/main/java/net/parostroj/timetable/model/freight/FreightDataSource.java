package net.parostroj.timetable.model.freight;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.FreightConnectionFilter;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

/**
 * Source of freight data for analysis.
 *
 * @author jub
 */
public interface FreightDataSource {

    Map<Train, List<FreightConnectionPath>> getFreightPassedInNode(TimeInterval fromInterval);

    List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval);

    List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval, FreightConnectionFilter filter);

    Collection<NodeConnectionEdges> getRegionConnectionEdges();

    Collection<NodeConnectionNodes> getRegionConnectionNodes();

    static FreightDataSource createCached(FreightDataSource source) {
        return new CachedFreightDataSource(source);
    }
}
