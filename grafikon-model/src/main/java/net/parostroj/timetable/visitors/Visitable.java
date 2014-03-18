package net.parostroj.timetable.visitors;

/**
 * Visitable objects.
 *
 * @author jub
 */
public interface Visitable {

    public void accept(TrainDiagramVisitor visitor);
}
