package net.parostroj.timetable.visitors;

/**
 * Visitable objects.
 *
 * @author jub
 */
public interface Visitable {

    void accept(TrainDiagramVisitor visitor);
}
