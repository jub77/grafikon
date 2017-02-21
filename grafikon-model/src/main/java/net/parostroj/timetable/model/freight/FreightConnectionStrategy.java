package net.parostroj.timetable.model.freight;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Strategy for freigt.
 *
 * @author jub
 */
public interface FreightConnectionStrategy {
    /**
     * Returns map with connections passed in the node for given time interval of train.
     *
     * @param fromInterval time interval of train for which the connections should be returned
     * @return map with passed freight in the node
     */
    Map<Train, List<FreightConnectionPath>> getFreightPassedInNode(TimeInterval fromInterval);

    /**
     * Returns connections started with given time interval.
     *
     * @param fromInterval time interval of train for which connections are computed
     * @return list of connections
     */
    List<FreightConnectionPath> getFreightToNodes(TimeInterval fromInterval);

    /**
     * Returns list of connections with given time interval limited for analysis purposes.
     *
     * @param fromInterval time interval of train for which connections are computed.
     * @return list of connections
     */
    List<FreightConnectionPath> getFreightToNodesNet(TimeInterval fromInterval);

    /**
     * @return collection of connections between regions (with edges through region centers)
     */
    Collection<NodeConnectionEdges> getRegionConnectionEdges();

    /**
     * @return collection of connections between regions (with only nodes on path - region centers)
     */
    Collection<NodeConnectionNodes> getRegionConnectionNodes();

    /**
     * Factory method for creating strategy based on type.
     *
     * @param type type of strategy
     * @param diagram train diagram
     * @return strategy
     */
    static FreightConnectionStrategy create(ConnectionStrategyType type, TrainDiagram diagram) {
        // TODO add other types
        return new BaseConnectionStrategy(diagram);
    }

    static FreightConnectionStrategy createCached(FreightConnectionStrategy strategy) {
        return new CachedConnectionStrategy(strategy);
    }
}
