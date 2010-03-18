package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.*;

/**
 * Abstract traversal visitor.
 *
 * @author jub
 */
public abstract class AbstractTrainDiagramTraversalVisitor
        extends AbstractTrainDiagramVisitor implements TrainDiagramTraversalVisitor {

    @Override
    public void visitAfter(TrainDiagram diagram) {
    }

    @Override
    public void visitAfter(Net net) {
    }

    @Override
    public void visitAfter(Node node) {
    }

    @Override
    public void visitAfter(Line line) {
    }
}
