package net.parostroj.timetable.model.freight;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.RegionHierarchy;
import net.parostroj.timetable.model.freight.FreightConnectionImpl.RegionsRegionHierarchy;

class NodeFreightImpl implements NodeFreight {

    private final Node from;
    private final Set<FreightConnectionVia> connections;

    NodeFreightImpl(Node from, Set<FreightConnectionVia> connections) {
        this.from = from;
        this.connections = Objects.requireNonNull(connections, "Connection cannot be null");
    }

    @Override
    public Node getFrom() {
        return from;
    }

    @Override
    public Set<FreightConnectionVia> getConnections() {
        return connections;
    }

    @Override
    public Set<FreightConnectionVia> getNodeConnections() {
        return connections.stream()
                .filter(c -> c.getTo().isNode())
                .collect(groupingBy(c -> c.getTo().getNode()))
                .values().stream()
                    .map(conns -> merge(conns,
                            c -> FreightFactory.createFreightDestination(c.getFrom(), c.getTo().getNode(), null)))
                    .collect(toSet());
    }

    @Override
    public Set<FreightConnectionVia> getRegionConnections() {
        return connections.stream()
                .filter(c -> c.getTo().isRegions())
                .collect(groupingBy(c -> c.getTo().getRegions()))
                .values().stream()
                .map(conns -> merge(conns, c -> FreightFactory.createFreightDestination(c.getFrom(),
                        RegionHierarchy.from(c.getTo().getRegions()))))
                .collect(toSet());
    }

    @Override
    public Set<FreightConnectionVia> getFreightColorConnections() {
        return connections.stream()
                .filter(c -> c.getTo().isFreightColors())
                .collect(groupingBy(c -> c.getTo().getFreightColors()))
                .values().stream()
                    .map(conns -> merge(conns,
                            c -> FreightFactory.createFreightDestination(c.getFrom(), c.getTo().getNode(),
                                    new RegionsRegionHierarchy(c.getTo().getRegions()))))
                    .collect(toSet());
    }

    private FreightConnectionVia merge(List<? extends FreightConnectionVia> connections,
            Function<FreightConnection, FreightDestination> destinationCreation) {
        if (connections.size() == 1) {
            return connections.get(0);
        } else {
            // merge
            FreightDestination destination = connections.stream()
                    .findAny()
                    .map(destinationCreation)
                    .orElse(null);
            Transport transport = connections.stream()
                    .map(FreightConnectionVia::getTransport)
                    .reduce(Transport::merge)
                    .orElse(null);
            Node lFrom = connections.stream().findAny().map(FreightConnectionVia::getFrom).orElse(null);
            return FreightFactory.createFreightNodeConnection(lFrom, destination, transport);
        }
    }

    @Override
    public String toString() {
        return connections.toString();
    }
}
