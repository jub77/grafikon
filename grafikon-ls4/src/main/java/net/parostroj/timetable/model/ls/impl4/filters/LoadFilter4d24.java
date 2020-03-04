package net.parostroj.timetable.model.ls.impl4.filters;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.ls.impl4.LoadFilter;

import java.util.Optional;

public class LoadFilter4d24 implements LoadFilter {

    @Override
    public void checkDiagram(TrainDiagram diagram, ModelVersion version) {
        if (version.compareTo(new ModelVersion(4, 24, 0)) <= 0) {
            // remove previous joined train (only next is kept)
            for (Train train : diagram.getTrains()) {
                train.getAttributes().setSkipListeners(true);
                try {
                    train.removeAttribute(Train.ATTR_PREVIOUS_JOINED_TRAIN);
                } finally {
                    train.getAttributes().setSkipListeners(false);
                }
            }
        }
        if (version.compareTo(new ModelVersion(4, 24, 2)) <= 0) {
            // add LEFT/RIGHT connector if missing
            for (Node node : diagram.getNet().getNodes()) {
                Optional<TrackConnector> left = node.getConnectors()
                        .find(c -> c.getOrientation() == Node.Side.LEFT);
                Optional<TrackConnector> right = node.getConnectors()
                        .find(c -> c.getOrientation() == Node.Side.RIGHT);
                if (!left.isPresent()) {
                    this.addConnector(diagram.getPartFactory(), node, "1", Node.Side.LEFT);
                }
                if (!right.isPresent()) {
                    this.addConnector(diagram.getPartFactory(), node, "2", Node.Side.RIGHT);
                }
            }
        }
    }

    private void addConnector(TrainDiagramPartFactory factory, Node node, String number, Node.Side orientation) {
        TrackConnector otherConnector = factory
                .createDefaultConnector(factory.createId(), node, number, orientation, Optional.empty());
        node.getConnectors().add(otherConnector);
    }
}
