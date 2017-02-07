package net.parostroj.timetable.model.freight;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
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

    public FreightConnectionAnalyser(TrainDiagram diagram) {
        this.analyser = new FreightAnalyser(diagram);
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
        FreightConnectionAnalysis analysis = new FreightConnectionAnalysis(analyser, from, to);
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
                return ctx.steps.stream().map(c -> (DirectNodeConnection) c).collect(toList());
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
        Stream<TrainPath> list = connections.stream().filter(NodeFreightConnection::isComplete).map(finder::find);
        return list.min(shortest()).orElse(EMPTY_PATH);
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
            TrainPath result = FreightFactory.createTrainPath(Collections.emptyList());
            for (DirectNodeConnection dnc : connection.getSteps()) {
                TrainPath selected = getClosest(current, dnc);
                current = selected.getEndTime() + shunt;
                result.addAll(selected);
            }
            return result;
        }

        private TrainPath getClosest(int time, DirectNodeConnection dnc) {
            return dnc.getConnections().stream()
                    .min(Comparator.comparingInt(tp -> TimeUtil.difference(time, tp.getStartTime())))
                    .orElse(null);
        }
    }

    private static TrainPath EMPTY_PATH = new EmptyPath();

    private static class EmptyPath extends AbstractList<TrainConnection> implements TrainPath {
        @Override
        public TrainConnection get(int index) {
            throw new ArrayIndexOutOfBoundsException();
        }

        @Override
        public int size() {
            return 0;
        }

    }
}
