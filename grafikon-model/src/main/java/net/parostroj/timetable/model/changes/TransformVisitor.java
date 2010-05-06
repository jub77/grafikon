package net.parostroj.timetable.model.changes;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainsCycle;
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
        DiagramChange.Action st = null;
        switch (event.getType()) {
            case TRAIN_ADDED: case TRAIN_REMOVED:
                change = new DiagramChange(DiagramChange.Type.TRAIN,
                        event.getType() == GTEventType.TRAIN_ADDED ? DiagramChange.Action.ADDED : DiagramChange.Action.REMOVED,
                        ((ObjectWithId)event.getObject()).getId());
                change.setObject(((Train)event.getObject()).getName());
                break;
            case TRAINS_CYCLE_ADDED: case TRAINS_CYCLE_REMOVED:
                change = new DiagramChange(DiagramChange.Type.TRAINS_CYCLE,
                        event.getType() == GTEventType.TRAINS_CYCLE_ADDED ? DiagramChange.Action.ADDED : DiagramChange.Action.REMOVED,
                        ((ObjectWithId)event.getObject()).getId());
                change.setObject(((TrainsCycle)event.getObject()).getName());
                break;
            case TRAIN_TYPE_ADDED: case TRAIN_TYPE_REMOVED: case TRAIN_TYPE_MOVED:
                st = event.getType() == GTEventType.TRAIN_TYPE_ADDED ?
                    DiagramChange.Action.ADDED :
                    (event.getType() == GTEventType.TRAIN_TYPE_REMOVED ? DiagramChange.Action.REMOVED : DiagramChange.Action.MOVED);
                change = new DiagramChange(DiagramChange.Type.TRAIN_TYPE, st,
                        ((ObjectWithId)event.getObject()).getId());
                change.setObject(getTrainTypeStr((TrainType)event.getObject()));
                break;
            case ENGINE_CLASS_ADDED: case ENGINE_CLASS_MOVED: case ENGINE_CLASS_REMOVED:
                st = event.getType() == GTEventType.ENGINE_CLASS_ADDED ?
                    DiagramChange.Action.ADDED :
                    (event.getType() == GTEventType.ENGINE_CLASS_REMOVED ? DiagramChange.Action.REMOVED : DiagramChange.Action.MOVED);
                change = new DiagramChange(DiagramChange.Type.ENGINE_CLASS, st,
                        ((ObjectWithId)event.getObject()).getId());
                change.setObject(((EngineClass)event.getObject()).getName());
                break;
            default:
                change = new DiagramChange(DiagramChange.Type.DIAGRAM, event.getSource().getId());
                change.setAction(DiagramChange.Action.MODIFIED);
        }
    }

    @Override
    public void visit(NetEvent event) {
        change = new DiagramChange(DiagramChange.Type.NET, event.getSource().getId());
        change.setAction(DiagramChange.Action.MODIFIED);
    }

    @Override
    public void visit(NodeEvent event) {
        change = new DiagramChange(DiagramChange.Type.NODE, event.getSource().getId());
        change.setObject(event.getSource().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
    }

    @Override
    public void visit(LineEvent event) {
        change = new DiagramChange(DiagramChange.Type.LINE, event.getSource().getId());
        Line line = event.getSource();
        change.setObject(line.getFrom().getName() + " - " + line.getTo().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
    }

    @Override
    public void visit(TrainEvent event) {
        change = new DiagramChange(DiagramChange.Type.TRAIN, event.getSource().getId());
        change.setObject(event.getSource().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
    }

    @Override
    public void visit(TrainTypeEvent event) {
        change = new DiagramChange(DiagramChange.Type.TRAIN_TYPE, event.getSource().getId());
        change.setObject(getTrainTypeStr(event.getSource()));
        change.setAction(DiagramChange.Action.MODIFIED);
    }

    @Override
    public void visit(TrainsCycleEvent event) {
        change = new DiagramChange(DiagramChange.Type.TRAINS_CYCLE, event.getSource().getId());
        change.setObject(event.getSource().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
    }

    @Override
    public void visit(TextItemEvent event) {
        change = new DiagramChange(DiagramChange.Type.TEXT_ITEM, event.getSource().getId());
        change.setObject(event.getSource().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
    }

    public DiagramChange getChange() {
        DiagramChange c = this.change;
        this.change = null;
        return c;
    }

    private String getTrainTypeStr(TrainType type) {
        return type.getAbbr() + " - " + type.getDesc();
    }
}
