package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.*;

/**
 * Traversal visitor.
 *
 * @author jub
 */
public interface TrainDiagramTraversalVisitor extends TrainDiagramVisitor {

    default void visitAfter(TrainDiagram diagram) {}

    default void visitAfter(Net net) {}

    default void visitAfter(Node node) {}

    default void visitAfter(Line line) {}
}
