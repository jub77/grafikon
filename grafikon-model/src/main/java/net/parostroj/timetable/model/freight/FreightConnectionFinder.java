package net.parostroj.timetable.model.freight;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.utils.Tuple;

/**
 * Helper class for getting connections between nodes. Results are cached for better performance.
 *
 * @author jub
 */
public class FreightConnectionFinder {

    private final FreightAnalyser analyser;

    private final Map<Tuple<Node>, List<FreightConnectionPath>> cache = new HashMap<>();
    private final Map<Node, List<FreightConnectionPath>> cacheF = new HashMap<>();
    private Collection<NodeConnectionEdges> cachedNet;

    public FreightConnectionFinder(FreightAnalyser analyser) {
        this.analyser = analyser;
    }

    /**
     * @return collection of connections between regions
     */
    public Collection<NodeConnectionEdges> getRegionConnections() {
        if (cachedNet == null) {
            cachedNet = analyser.getConnectionStrategy().getRegionConnectionEdges();
        }
        return cachedNet;
    }

    /**
     * @param node source node
     * @return all freight connections from specified node which exist
     */
    public List<FreightConnectionPath> getConnectionFrom(Node node) {
        if (cacheF.containsKey(node)) {
            return cacheF.get(node);
        } else {
            List<FreightConnectionPath> connSet = analyser.getFreightIntervalsFrom(node).stream()
                    .flatMap(i -> analyser.getConnectionStrategy().getFreightToNodesNet(i).stream())
                    .collect(toList());
            cacheF.put(node, connSet);
            return connSet;
        }
    }

    /**
     * @param sourceNode source node
     * @param targetNode target node
     * @return all freight connections which connect specified nodes
     */
    public List<FreightConnectionPath> getConnectionFromTo(Node sourceNode, Node targetNode) {
        Tuple<Node> fromTo = new Tuple<>(sourceNode, targetNode);
        if (cache.containsKey(fromTo)) {
            return cache.get(fromTo);
        } else {
            List<FreightConnectionPath> connSet = getConnectionFrom(sourceNode).stream()
                    .filter(c -> c.getTo().getNode() == targetNode)
                    .collect(toList());
            cache.put(fromTo, connSet);
            return connSet;
        }
    }
}
