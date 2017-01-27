package net.parostroj.timetable.model.freight;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.freight.FreightConnectionImpl.RegionsRegionHierarchy;

class NodeFreightImpl implements NodeFreight {

    private final Set<FreightConnectionVia> connections;

    NodeFreightImpl(Set<FreightConnectionVia> connections) {
        Preconditions.checkNotNull(connections, "Connection cannot be null");
        this.connections = connections;
    }

    @Override
    public Set<FreightConnectionVia> getConnections() {
        return connections;
    }

    @Override
    public Set<FreightConnectionVia> getNodeConnections() {
        return connections.stream()
                .filter(c -> c.getTo().isNode())
                .collect(Collectors.groupingBy(c -> c.getTo().getNode()))
                .values().stream()
                    .map(conns -> merge(conns,
                            c -> FreightFactory.createFreightDestination(c.getFrom(), c.getTo().getNode(), null)))
                    .collect(Collectors.toSet());
    }

    private FreightConnectionVia merge(List<? extends FreightConnectionVia> connections,
            Function<FreightConnection, FreightDestination> destinationCreation) {
        if (connections.size() == 1) {
            return connections.get(0);
        } else {
            // merge
            FreightDestination destination = connections.stream()
                    .findAny()
                    .map(c -> destinationCreation.apply(c))
                    .get();
            Transport transport = connections.stream()
                    .map(c -> c.getTransport())
                    .reduce((t1, t2) -> t1.merge(t2))
                    .get();
            Node from = connections.stream().findAny().map(c -> c.getFrom()).get();
            return FreightFactory.createFreightNodeConnection(from, destination, transport);
        }
    }

    @Override
    public Set<FreightConnectionVia> getRegionConnections() {
        return connections.stream()
                .filter(c -> c.getTo().isRegions())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FreightConnectionVia> getFreightColorConnections() {
        return connections.stream()
                .filter(c -> c.getTo().isFreightColors())
                .collect(Collectors.groupingBy(c -> c.getTo().getFreightColors()))
                .values().stream()
                    .map(conns -> merge(conns,
                            c -> FreightFactory.createFreightDestination(c.getFrom(), c.getTo().getNode(),
                                    new RegionsRegionHierarchy(c.getTo().getRegions()))))
                    .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return connections.toString();
    }
}
