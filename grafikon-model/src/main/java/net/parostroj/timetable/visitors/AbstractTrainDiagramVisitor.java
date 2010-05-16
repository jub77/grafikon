package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.*;

/**
 * Abstract class for visitors - in order not to always implement all methods of
 * the visitor.
 *
 * @author jub
 */
public abstract class AbstractTrainDiagramVisitor implements TrainDiagramVisitor {

    @Override
    public void visit(TrainDiagram diagram) {
    }

    @Override
    public void visit(Net net) {
    }

    @Override
    public void visit(Train train) {
    }

    @Override
    public void visit(Node node) {
    }

    @Override
    public void visit(Line line) {
    }

    @Override
    public void visit(LineTrack track) {
    }

    @Override
    public void visit(NodeTrack track) {
    }

    @Override
    public void visit(TrainType type) {
    }

    @Override
    public void visit(Route route) {
    }

    @Override
    public void visit(EngineClass engineClass) {
    }

    @Override
    public void visit(TrainsCycle cycle) {
    }

    @Override
    public void visit(TextItem item) {
    }

    @Override
    public void visit(TimetableImage image) {
    }

    @Override
    public void visit(LineClass lineClass) {
    }
}
