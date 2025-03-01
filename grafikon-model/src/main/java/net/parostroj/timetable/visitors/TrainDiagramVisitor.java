package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.*;

/**
 * Train diagram visitor.
 *
 * @author jub
 */
public interface TrainDiagramVisitor {

    default void visit(TrainDiagram diagram) {}

    default void visit(Net net) {}

    default void visit(Train train) {}

    default void visit(Node node) {}

    default void visit(Line line) {}

    default void visit(LineTrack track) {}

    default void visit(NodeTrack track) {}

    default void visit(TrainType type) {}

    default void visit(Route route) {}

    default void visit(EngineClass engineClass) {}

    default void visit(TrainsCycle cycle) {}

    default void visit(TextItem item) {}

    default void visit(TimetableImage image) {}

    default void visit(LineClass lineClass) {}

    default void visit(OutputTemplate template) {}

    default void visit(TrainsCycleType type) {}

    default void visit(Group group) {}

    default void visit(FreightNet net) {}

    default void visit(Region region) {}

    default void visit(Company company) {}

    default void visit(Output output) {}

    default void visit(TrackConnector connector) {}
}
