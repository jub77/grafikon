package net.parostroj.timetable.model.freight;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.parostroj.timetable.model.FreightDestination;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Pair;

/**
 * @author jub
 */
public class FreightAnalyser {

    private final TrainDiagram diagram;

    public FreightAnalyser(final TrainDiagram diagram) {
        this.diagram = diagram;
    }

    public List<TimeInterval> getFreightIntervalsFrom(Node node) {
        return StreamSupport.stream(node.spliterator(), true)
                .filter(i -> !i.isTechnological() && i.isFreightFrom())
                .sorted(FreightAnalyser::compareNormalizedStarts)
                .collect(Collectors.toList());
    }

    public Map<FreightDestination, Set<TimeInterval>> getFreightFrom(Node node) {
         return getFreightIntervalsFrom(node).stream()
            .flatMap(i -> diagram.getFreightNet()
                    .getFreightToNodes(i).stream()
                    .map(d -> new Pair<>(d, i)))
            .collect(Collectors.toMap(t -> t.first, t -> Collections.singleton(t.second), (v1, v2) -> {
                if (v1 instanceof HashSet) {
                    v1.addAll(v2);
                    return v1;
                } else if (v2 instanceof HashSet) {
                    v2.addAll(v1);
                    return v2;
                } else {
                    Set<TimeInterval> set = new HashSet<>();
                    set.addAll(v1);
                    set.addAll(v2);
                    return set;
                }
            }));
    }

    protected static int compareNormalizedStarts(TimeInterval i1, TimeInterval i2) {
        return Integer.compare(i1.getInterval().getNormalizedStart(), i2.getInterval().getNormalizedStart());
    }
}
