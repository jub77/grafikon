package net.parostroj.timetable.model.freight;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.parostroj.timetable.model.Script;
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
        return switch (type) {
            case BASE -> new BaseConnectionStrategy(diagram);
            case REGION -> new RegionToConnectionStrategy(diagram);
            case CUSTOM_CONNECTION_FILTER -> new CustomNetFilterConnectionStrategy(diagram);
        };
    }

    static FreightConnectionStrategy create(ConnectionStrategyType type, TrainDiagram diagram, Script script) {
        FreightConnectionStrategy strategy;
        if (Objects.requireNonNull(type) == ConnectionStrategyType.CUSTOM_CONNECTION_FILTER) {
            strategy = new CustomNetFilterConnectionStrategy(diagram, script);
        } else {
            throw new IllegalArgumentException("Type " + type + " does not have script");
        }
        return strategy;
    }

    /**
     * Creates wrapper around original strategy which caches the values from original strategy. The values in the cache
     * are not refreshed when the diagram changes. So it is useful only in the meantime until next diagram change,
     *
     * @param strategy original strategy
     * @return cached strategy
     */
    static FreightConnectionStrategy createCached(FreightConnectionStrategy strategy) {
        return new CachedConnectionStrategy(strategy);
    }
}
