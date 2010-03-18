package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.*;

/**
 * Train diagram visitor.
 *
 * @author jub
 */
public interface TrainDiagramVisitor {

    public void visit(TrainDiagram diagram);

    public void visit(Net net);

    public void visit(Train train);

    public void visit(Node node);

    public void visit(Line line);

    public void visit(LineTrack track);

    public void visit(NodeTrack track);

    public void visit(TrainType type);

    public void visit(Route route);

    public void visit(EngineClass engineClass);

    public void visit(TrainsCycle cycle);
}
