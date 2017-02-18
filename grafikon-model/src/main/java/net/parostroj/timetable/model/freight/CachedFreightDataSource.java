package net.parostroj.timetable.model.freight;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.FreightConnectionFilter;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

/**
 * Wrapper which caches the data from data source.
 *
 * @author jub
 */
class CachedFreightDataSource implements FreightDataSource {

    private final FreightDataSource source;

    private Collection<NodeConnectionNodes> connNodes;
    private Collection<NodeConnectionEdges> connEdges;
    private final Map<TimeInterval, List<FreightConnectionPath>> freightToNodes;
    private final Map<TimeInterval, Map<Train, List<FreightConnectionPath>>> passedInNode;

    public CachedFreightDataSource(FreightDataSource source) {
        this.source = source;
        this.freightToNodes = new HashMap<>();
        this.passedInNode = new HashMap<>();
    }

    @Override
    public Map<Train, List<FreightConnectionPath>> getFreightPassedInNode(TimeInterval fromInterval) {
        if (!passedInNode.containsKey(fromInterval)) {
            Map<Train, List<FreightConnectionPath>> pin = source.getFreightPassedInNode(fromInterval);
            passedInNode.put(fromInterval, pin);
            return pin;
        } else {
            return passedInNode.get(fromInterval);
        }
    }

    @Override
    public List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval) {
        if (!freightToNodes.containsKey(fromInterval)) {
            List<FreightConnectionPath> ftn = source.getFreightToNodes(fromInterval);
            freightToNodes.put(fromInterval, ftn);
            return ftn;
        } else {
            return freightToNodes.get(fromInterval);
        }
    }

    @Override
    public List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval, FreightConnectionFilter filter) {
        // no caching available
        return source.getFreightToNodes(fromInterval, filter);
    }

    @Override
    public Collection<NodeConnectionEdges> getRegionConnectionEdges() {
        if (connEdges == null) {
            connEdges = source.getRegionConnectionEdges();
        }
        return connEdges;
    }

    @Override
    public Collection<NodeConnectionNodes> getRegionConnectionNodes() {
        if (connNodes == null) {
            connNodes = source.getRegionConnectionNodes();
        }
        return connNodes;
    }
}
