package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.*;

/**
 * Train diagram visitor.
 *
 * @author jub
 */
public interface TrainDiagramVisitor {

    void visit(TrainDiagram diagram);

    void visit(Net net);

    void visit(Train train);

    void visit(Node node);

    void visit(Line line);

    void visit(LineTrack track);

    void visit(NodeTrack track);

    void visit(TrainType type);

    void visit(Route route);

    void visit(EngineClass engineClass);

    void visit(TrainsCycle cycle);

    void visit(TextItem item);

    void visit(TimetableImage image);

    void visit(LineClass lineClass);

    void visit(OutputTemplate template);

    void visit(TrainsCycleType type);

    void visit(Group group);

    void visit(FreightNet net);

    void visit(Region region);

    void visit(Company company);

    void visit(Output output);

    void visit(TrackConnector connector);
}
