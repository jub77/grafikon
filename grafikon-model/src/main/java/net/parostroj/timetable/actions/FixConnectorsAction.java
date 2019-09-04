package net.parostroj.timetable.actions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Heuristic fix for connectors - not really fix, but best attempt to correct
 * missing information about orientation of connectors.
 *
 * @author jub
 */
public class FixConnectorsAction {

    private static final Logger log = LoggerFactory.getLogger(FixConnectorsAction.class);

    public void fixConnectors(TrainDiagram diagram) {
        // for all versions
        for (Node node : diagram.getNet().getNodes()) {
            this.fixConnectors(node);
        }
    }

    private void fixConnectors(Node node) {
        Map<Line, List<TrackConnector>> map = node.getConnectors().stream()
                .filter(c -> c.getLineTrack().isPresent())
                .collect(Collectors.groupingBy(c -> c.getLineTrack().get().getOwner()));
        int compare = this.checkConnectors(map);
        if (compare != 0 && map.size() > 1) {
            log.debug("Fixing connectors on node: {} (size={}, compare={})", node, map.size(),
                    compare);
            // move one to other side
            map.values().iterator().next().forEach(
                    c -> c.setOrientation(compare == -1 ? Node.Side.RIGHT : Node.Side.LEFT));
        }
    }

    // compare (-1 all left, 1 all right, 0 otherwise)
    private int checkConnectors(Map<Line, List<TrackConnector>> map) {
        int count = map.size();
        int leftCount = (int) map.values().stream()
                .filter(c -> c.get(0).getOrientation() == Node.Side.LEFT).count();
        if (leftCount == 0) {
            return 1;
        } else if (leftCount == count) {
            return -1;
        }
        return 0;
    }
}
