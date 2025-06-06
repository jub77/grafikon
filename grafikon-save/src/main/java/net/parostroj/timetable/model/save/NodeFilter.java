package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrackConnector;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramPartFactory;
import net.parostroj.timetable.model.ls.ModelVersion;

import java.util.Optional;

/**
 * Node filter - add missing connectors.
 *
 * @author jub
 */
public class NodeFilter implements TrainDiagramFilter {
    @Override
    public TrainDiagram filter(TrainDiagram diagram, ModelVersion version) {
        // add LEFT/RIGHT connector if missing
        for (Node node : diagram.getNet().getNodes()) {
            Optional<TrackConnector> left = node.getConnectors()
                    .find(c -> c.getOrientation() == Node.Side.LEFT);
            Optional<TrackConnector> right = node.getConnectors()
                    .find(c -> c.getOrientation() == Node.Side.RIGHT);
            if (left.isEmpty()) {
                this.addConnector(diagram.getPartFactory(), node, "1", Node.Side.LEFT);
            }
            if (right.isEmpty()) {
                this.addConnector(diagram.getPartFactory(), node, "2", Node.Side.RIGHT);
            }
        }
        return diagram;
    }

    private void addConnector(TrainDiagramPartFactory factory, Node node, String number, Node.Side orientation) {
        TrackConnector otherConnector = factory
                .createDefaultConnector(factory.createId(), node, number, orientation, null);
        node.getConnectors().add(otherConnector);
    }
}
