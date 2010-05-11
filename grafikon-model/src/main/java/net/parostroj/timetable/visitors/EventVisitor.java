package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.events.*;

/**
 * Event visitor.
 *
 * @author jub
 */
public interface EventVisitor {

    public void visit(TrainDiagramEvent event);

    public void visit(NetEvent event);

    public void visit(NodeEvent event);

    public void visit(LineEvent event);

    public void visit(TrainEvent event);

    public void visit(TrainTypeEvent event);

    public void visit(TrainsCycleEvent event);

    public void visit(TextItemEvent event);

    public void visit(EngineClassEvent event);
}
