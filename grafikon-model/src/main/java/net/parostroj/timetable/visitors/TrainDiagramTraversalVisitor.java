package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.*;

/**
 * Traversal visitor.
 *
 * @author jub
 */
public interface TrainDiagramTraversalVisitor extends TrainDiagramVisitor {

    public void visitAfter(TrainDiagram diagram);

    public void visitAfter(Net net);

    public void visitAfter(Node node);

    public void visitAfter(Line line);
}
