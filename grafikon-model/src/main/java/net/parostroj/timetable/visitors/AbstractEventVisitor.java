package net.parostroj.timetable.visitors;

import net.parostroj.timetable.model.events.*;

/**
 * Abstract event visitor.
 *
 * @author jub
 */
public abstract class AbstractEventVisitor implements EventVisitor {

    @Override
    public void visit(TrainDiagramEvent event) {
    }

    @Override
    public void visit(NetEvent event) {
    }

    @Override
    public void visit(NodeEvent event) {
    }

    @Override
    public void visit(LineEvent event) {
    }

    @Override
    public void visit(TrainEvent event) {
    }

    @Override
    public void visit(TrainTypeEvent event) {
    }

    @Override
    public void visit(TrainsCycleEvent event) {
    }

    @Override
    public void visit(OutputTemplateEvent event) {
    }

    @Override
    public void visit(EngineClassEvent event) {
    }

    @Override
    public void visit(TextItemEvent event) {
    }

    @Override
    public void visit(FreightNetEvent event) {
    }
}
