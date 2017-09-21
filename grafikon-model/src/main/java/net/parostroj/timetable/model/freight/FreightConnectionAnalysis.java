package net.parostroj.timetable.model.freight;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static net.parostroj.timetable.utils.ObjectsUtil.intersects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;

/**
 * Analysis of freight connection between two stations. It produces path and information
 * if the destination is reachable or not (if the path is complete).
 *
 * @author jub
 */
class FreightConnectionAnalysis {

    private final Node from;
    private final Node to;
    private final FreightConnectionFinder finder;

    FreightConnectionAnalysis(FreightConnectionFinder finder, Node from, Node to) {
        this.finder = finder;
        this.from = from;
        this.to = to;
    }

    private final Collection<Context> allContexts = new ArrayList<>();
    private final Queue<Context> notFinishedContexts = new LinkedList<>();

    Context createContext() {
        Context context = new Context();
        allContexts.add(context);
        return context;
    }

    Context copyContext(Context context) {
        Context newContext = context.copy();
        allContexts.add(newContext);
        notFinishedContexts.add(newContext);
        return newContext;
    }

    void init(Context context) {
        if(from == to) {
            context.stage = Stage.CONNECTION;
            return;
        }
        List<FreightConnectionPath> conns = finder.getConnectionFromTo(from, to);
        if (!conns.isEmpty()) {
            context.stage = Stage.TO_NODE;
        } else {
            context.stage = from.isCenterOfRegions() ? Stage.BETWEEN_CENTERS : Stage.TO_CENTER;
        }
    }

    void toNode(Context context) {
        Set<TrainPath> set = finder.getConnectionFromTo(context.current, to).stream()
                .map(FreightConnectionPath::getPath).collect(toSet());
        if (!set.isEmpty()) {
            context.steps.add(new StepImpl(context.current, to, set));
            context.stage = Stage.CONNECTION;
        } else {
            context.stage = Stage.NO_CONNECTION;
        }
    }

    void toCenter(final Context context) {
        Collection<ToRegionConnection> regions = getToCenterRegions(context);
        if (regions.isEmpty()) {
            context.stage = Stage.NO_CONNECTION;
        } else {
            for (ToRegionConnection conn : regions) {
                Context nContext = conn.context;
                Node centerNode = conn.center;
                Set<TrainPath> set = finder.getConnectionFromTo(nContext.current, centerNode).stream()
                        .map(FreightConnectionPath::getPath).collect(toSet());
                if (!set.isEmpty()) {
                    nContext.steps.add(new StepImpl(nContext.current, centerNode, set));
                    nContext.current = centerNode;
                    boolean noDirectConnection = finder.getConnectionFromTo(nContext.current, to).isEmpty();
                    if (noDirectConnection && conn.transitive) {
                        nContext.stage = Stage.BETWEEN_CENTERS;
                    } else if (conn.transitive || intersects(to.getRegions(), centerNode.getCenterRegions())) {
                        nContext.stage = Stage.TO_NODE;
                    } else {
                        nContext.stage = Stage.NO_CONNECTION;
                    }
                } else {
                    nContext.stage = Stage.NO_CONNECTION;
                }
            }
        }
    }

    void betweenCenters(final Context context) {
        Collection<ToRegionConnection> regions = this.getBetweenCenterRegions(context);
        if (regions.isEmpty()) {
            context.stage = Stage.NO_CONNECTION;
        } else {
            for (ToRegionConnection pair : regions) {
                Context nContext = pair.context;
                Node centerNode = pair.center;
                Optional<NodeConnectionEdges> regionConn = finder.getRegionConnections().stream()
                        .filter(c -> c.getFrom() == nContext.current && c.getTo() == centerNode)
                        .findAny();
                if (regionConn.isPresent()) {
                    NodeConnectionEdges conn = regionConn.get();
                    conn.getEdges().stream()
                        .map(StepImpl::new)
                        .forEach(nContext.steps::add);
                    nContext.current = centerNode;
                    nContext.stage = centerNode == to ? Stage.CONNECTION : Stage.TO_NODE;
                } else {
                    nContext.stage = Stage.NO_CONNECTION;
                }
            }
        }
    }

    Context getNextContext() {
        return notFinishedContexts.poll();
    }

    Collection<Context> getContexts() {
        return allContexts;
    }

    // returns regions - for current node and if there is a direct connection to a center of region which is the center
    // of region of destination node
    private Collection<ToRegionConnection> getToCenterRegions(Context context) {
        ContextSource source = new ContextSource(context);

        Set<Node> nodeCenters = context.current.getRegions().stream()
                .map(Region::getCenterNode)
                .filter(Objects::nonNull)
                .collect(toSet());

        Stream<Node> otherCenters = finder.getConnectionFrom(context.current).stream()
            .map(FreightConnectionPath::getTo)
            .filter(d -> d.isNode() && d.isRegions())
            .map(FreightDestination::getNode)
            .filter(n -> !nodeCenters.contains(n));

        Stream<ToRegionConnection> toRegionConnNode = nodeCenters.stream()
                .map(n -> new ToRegionConnection(source.getContext(), n, true));
        Stream<ToRegionConnection> toRegionConnOther = otherCenters
                .map(n -> new ToRegionConnection(source.getContext(), n, false));

        return Stream.concat(toRegionConnNode, toRegionConnOther).collect(toList());
    }

    private Collection<ToRegionConnection> getBetweenCenterRegions(Context context) {
        ContextSource source = new ContextSource(context);
        return to.getRegions().stream()
                .map(Region::getCenterNode)
                .filter(Objects::nonNull)
                .map(n -> new ToRegionConnection(source.getContext(), n))
                .collect(toList());
    }

    enum Stage {
        START, TO_NODE, TO_CENTER, BETWEEN_CENTERS, CONNECTION, NO_CONNECTION;
    }

    class Context {
        public Node current;
        public Stage stage;

        public final List<StepImpl> steps = new ArrayList<>();

        public Context() {
            this.current = from;
            this.stage = Stage.START;
        }

        public Context copy() {
            Context context = new Context();
            context.current = current;
            context.stage = stage;
            context.steps.addAll(steps);
            return context;
        }

        @Override
        public String toString() {
            return String.format("%s,%s", current, stage);
        }
    }

    static class StepImpl implements DirectNodeConnection {
        public final Node from;
        public final Node to;
        public final Set<TrainPath> connections;

        public StepImpl(DirectNodeConnection dc) {
            this(dc.getFrom(), dc.getTo(), dc.getConnections());
        }

        public StepImpl(Node from, Node to, Set<TrainPath> connections) {
            this.from = from;
            this.to = to;
            this.connections = connections;
        }

        @Override
        public Set<TrainPath> getConnections() {
            return connections;
        }

        @Override
        public Node getFrom() {
            return from;
        }

        @Override
        public Node getTo() {
            return to;
        }

        public int getWeight() {
            return connections.stream()
                    .mapToInt(c -> c.stream()
                            .mapToInt(tc -> tc.getTo().getStart() - tc.getFrom().getEnd())
                            .sum())
                    .min()
                    .orElse(Integer.MAX_VALUE);
        }

        @Override
        public String toString() {
            return connections.toString();
        }
    }

    private static class ToRegionConnection {
        public final Context context;
        public final Node center;
        public final boolean transitive;

        public ToRegionConnection(Context context, Node center, boolean transitive) {
            this.context = context;
            this.center = center;
            this.transitive = transitive;
        }

        public ToRegionConnection(Context context, Node center) {
            this(context, center, false);
        }

        @Override
        public String toString() {
            return String.format("%s,%s,%s", context, center, transitive);
        }
    }

    private class ContextSource {
        private final Context source;
        private Context current;

        ContextSource(Context source) {
            this.source = source;
        }

        synchronized Context getContext() {
            if (current == null) {
                current = source;
            } else {
                current = copyContext(current);
            }
            return current;
        }
    }
}
