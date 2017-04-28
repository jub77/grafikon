package net.parostroj.timetable.model.freight;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.freight.FreightConnectionAnalysis.Context;
import net.parostroj.timetable.model.freight.FreightConnectionAnalysis.Stage;
import net.parostroj.timetable.model.freight.FreightConnectionAnalysis.StepImpl;
import net.parostroj.timetable.utils.TimeUtil;

/**
 * Analyser for freight connections between stations.
 *
 * @author jub
 */
public class FreightConnectionAnalyser {

    private final FreightAnalyser analyser;

    public FreightConnectionAnalyser(final FreightConnectionStrategy strategy) {
        this.analyser = new FreightAnalyser(strategy);
    }

    FreightConnectionAnalyser(FreightAnalyser analyser) {
        this.analyser = analyser;
    }

    /**
     * Returns freight connections between stations - if some exists.
     *
     * @param from start node
     * @param to end node
     * @return shortest freight connection
     */
    public Set<NodeFreightConnection> analyse(Node from, Node to) {
        FreightConnectionAnalysis analysis = new FreightConnectionAnalysis(
                new FreightConnectionFinder(analyser),
                from,
                to);
        Context context = analysis.createContext();

        while (context != null) {
            switch (context.stage) {
            case START:
                analysis.init(context);
                break;
            case TO_NODE:
                analysis.toNode(context);
                break;
            case TO_CENTER:
                analysis.toCenter(context);
                break;
            case BETWEEN_CENTERS:
                analysis.betweenCenters(context);
                break;
            case CONNECTION: case NO_CONNECTION:
                // get next context
                context = analysis.getNextContext();
                break;
            default:
                throw new IllegalStateException("Unknown state");
            }
        }

        return analysis.getContexts().stream().<NodeFreightConnection>map(ctx -> new NodeFreightConnection() {

            private Integer weight;

            @Override
            public Node getFrom() {
                return from;
            }

            @Override
            public Node getTo() {
                return to;
            }

            @Override
            public List<DirectNodeConnection> getSteps() {
                return ctx.steps.stream().collect(toList());
            }

            @Override
            public boolean isComplete() {
                return ctx.stage == Stage.CONNECTION;
            }

            @Override
            public int getLength() {
                if (weight == null) {
                    weight = isComplete() ? getLengthOfPath(ctx.steps) : Integer.MAX_VALUE;
                }
                return weight.intValue();
            }

            private int getLengthOfPath(List<StepImpl> steps) {
                return steps.stream().mapToInt(StepImpl::getWeight).sum();
            }
        }).collect(toSet());
    }

    public TrainPath getTrainPath(Collection<? extends NodeFreightConnection> connections, int start, int shunt) {
        ConnectionFinder finder = new ConnectionFinder(start, shunt);
        Stream<TrainPath> stream = connections.stream()
                .filter(NodeFreightConnection::isComplete)
                .map(finder::find);
        return stream
                .min(shortest())
                .orElseGet(TrainPath::empty);
    }

    private Comparator<TrainPath> shortest() {
        return Comparator.comparingInt(TrainPath::getLength);
    }

    private static class ConnectionFinder {

        private final int start;
        private final int shunt;

        public ConnectionFinder(int start, int shunt) {
            this.start = start;
            this.shunt = shunt;
        }

        public TrainPath find(NodeFreightConnection connection) {
            int current = start;
            TrainPath result = FreightFactory.createTrainPath(emptyList());
            TrainPath last = null;
            for (DirectNodeConnection currentSet : connection.getSteps()) {
                final int currentStart = current;
                TrainPath selected = getSame(last, currentSet)
                        .orElseGet(() -> getClosest(currentStart, currentSet)
                                .orElse(null));
                if (selected == null) {
                    throw new IllegalArgumentException("Only complete connections allowed.");
                }
                // increase the time for with shunt time
                current = selected.getEndTime() + shunt;
                result.addAll(selected);
                last = selected;
            }
            return result;
        }

        /**
         * @return the same train (if the same train can be used for the next part, shunt time is ignored)
         */
        private Optional<TrainPath> getSame(TrainPath last, DirectNodeConnection currentSet) {
            if (last == null) {
                return Optional.empty();
            } else {
                Train lastTrain = last.getLast().getTrain();
                return currentSet.getConnections().stream()
                        .filter(c -> c.getFirst().getTrain() == lastTrain)
                        .findAny();
            }
        }

        /**
         * @return train which is closest the the time specified
         */
        private Optional<TrainPath> getClosest(int time, DirectNodeConnection dnc) {
            return dnc.getConnections().stream()
                    .min(Comparator.comparingInt(tp -> TimeUtil.difference(time, tp.getStartTime())));
        }
    }
}
