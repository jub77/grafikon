package net.parostroj.timetable.model.freight;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Collectors;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.freight.FreightConnectionAnalysis.Context;
import net.parostroj.timetable.model.freight.FreightConnectionAnalysis.Stage;
import net.parostroj.timetable.model.freight.FreightConnectionAnalysis.StepImpl;

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

    /**
     * Returns shortest freight connection between stations - if one exists.
     *
     * @param from start node
     * @param to end node
     * @return shortest freight connection
     */
    public NodeFreightConnection analyse(Node from, Node to) {
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

        // select shortest connection
        Context resultContext = analysis.getContexts().stream()
                .collect(Collectors.minBy(this::compareLengthOfPath))
                .get();
        return new NodeFreightConnection() {
            @Override
            public Node getFrom() {
                return from;
            }

            @Override
            public Node getTo() {
                return to;
            }

            @Override
            public List<Step> getSteps() {
                return resultContext.steps.stream().map(c -> (Step) c).collect(toList());
            }

            @Override
            public boolean isComplete() {
                return resultContext.stage == Stage.CONNECTION;
            }
        };
    }

    private int compareLengthOfPath(Context a, Context b) {
        return Integer.compare(getLengthOfPath(a.steps), getLengthOfPath(b.steps));
    }

    private int getLengthOfPath(List<StepImpl> steps) {
        return steps.stream().mapToInt(StepImpl::getWeight).sum();
    }
}
