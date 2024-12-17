package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.*;

/**
 * Traversal visitor.
 *
 * @author jub
 */
public interface TrainDiagramTraversalVisitor extends TrainDiagramVisitor {

    void visitAfter(TrainDiagram diagram);

    void visitAfter(Net net);

    void visitAfter(Node node);

    void visitAfter(Line line);
}
