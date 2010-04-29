package net.parostroj.timetable.net;

import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Transforms diagram event into DiagramChange.
 *
 * @author jub
 */
public class TransformVisitor implements EventVisitor {

    private DiagramChange change;

    @Override
    public void visit(TrainDiagramEvent event) {
        change = new DiagramChange(DiagramChange.DiagramChangeType.DIAGRAM, event.getSource().getId());
        change.setObjectKey("diagram");
    }

    @Override
    public void visit(NetEvent event) {
        change = new DiagramChange(DiagramChange.DiagramChangeType.NET, event.getSource().getId());
        change.setObjectKey("net");
    }

    @Override
    public void visit(NodeEvent event) {
        change = new DiagramChange(DiagramChange.DiagramChangeType.NODE, event.getSource().getId());
        change.setObject(event.getSource().getName());
    }

    @Override
    public void visit(LineEvent event) {
        change = new DiagramChange(DiagramChange.DiagramChangeType.LINE, event.getSource().getId());
        change.setObject(event.getSource().getFrom().getAbbr() + " - " + event.getSource().getTo().getAbbr());
    }

    @Override
    public void visit(TrainEvent event) {
        change = new DiagramChange(DiagramChange.DiagramChangeType.TRAIN, event.getSource().getId());
        change.setObject(event.getSource().getName());
    }

    @Override
    public void visit(TrainTypeEvent event) {
        change = new DiagramChange(DiagramChange.DiagramChangeType.TRAIN_TYPE, event.getSource().getId());
        change.setObject(event.getSource().getAbbr() + " - " + event.getSource().getDesc());
    }

    @Override
    public void visit(TrainsCycleEvent event) {
        change = new DiagramChange(DiagramChange.DiagramChangeType.TRAINS_CYCLE, event.getSource().getId());
        change.setObject(event.getSource().getName());
    }

    @Override
    public void visit(TextItemEvent event) {
        change = new DiagramChange(DiagramChange.DiagramChangeType.TEXT_ITEM, event.getSource().getId());
        change.setObject(event.getSource().getName());
    }

    public DiagramChange getChange() {
        DiagramChange c = this.change;
        this.change = null;
        return c;
    }
}
