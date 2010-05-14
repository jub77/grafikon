package net.parostroj.timetable.model.changes;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Transforms diagram event into DiagramChange.
 *
 * @author jub
 */
public class TransformVisitor implements EventVisitor {

    private DiagramChange change;
    private EventToChangeConvert converter = new EventToChangeConvert();

    @Override
    public void visit(TrainDiagramEvent event) {
        DiagramChange.Type type = converter.getType(event.getType());
        DiagramChange.Action action = converter.getAction(event.getType());
        if (type != null) {
            if (action == null)
                throw new IllegalArgumentException("Action missing: " + event.getType());
            change = new DiagramChange(type, action, ((ObjectWithId)event.getObject()).getId());
            // get name
            change.setObject(this.getObjectStr(event.getObject()));
        } else {
            change = new DiagramChange(DiagramChange.Type.DIAGRAM, action, event.getSource().getId());
            if (action == null)
                throw new IllegalArgumentException("Action missing: " + event.getType());
            this.addDescription(event);
        }
    }

    @Override
    public void visit(NetEvent event) {
        DiagramChange.Type type = converter.getType(event.getType());
        DiagramChange.Action action = converter.getAction(event.getType());
        if (type != null) {
            if (action == null)
                throw new IllegalArgumentException("Action missing: " + event.getType());
            change = new DiagramChange(type, action, ((ObjectWithId)event.getObject()).getId());
            // get name
            change.setObject(this.getObjectStr(event.getObject()));
        }
    }

    @Override
    public void visit(NodeEvent event) {
        change = new DiagramChange(DiagramChange.Type.NODE, event.getSource().getId());
        change.setObject(event.getSource().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visit(LineEvent event) {
        change = new DiagramChange(DiagramChange.Type.LINE, event.getSource().getId());
        Line line = event.getSource();
        change.setObject(line.getFrom().getName() + " - " + line.getTo().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
    }

    @Override
    public void visit(TrainEvent event) {
        change = new DiagramChange(DiagramChange.Type.TRAIN, event.getSource().getId());
        change.setObject(event.getSource().getName());
        change.setAction(DiagramChange.Action.MODIFIED);
        this.addDescription(event);
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

    @Override
    public void visit(EngineClassEvent event) {
        change = new DiagramChange(DiagramChange.Type.ENGINE_CLASS, event.getSource().getId());
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

    private String getObjectStr(Object object) {
        if (object instanceof Train) {
            return ((Train)object).getCompleteName();
        } else if (object instanceof TrainsCycle) {
            return ((TrainsCycle)object).getName();
        } else if (object instanceof TrainType) {
            return this.getTrainTypeStr((TrainType)object);
        } else if (object instanceof EngineClass) {
            return ((EngineClass)object).getName();
        } else if (object instanceof TextItem) {
            return ((TextItem)object).getName();
        } else if (object instanceof Line) {
            Line line = (Line)object;
            return line.getFrom().getAbbr() + " - " + line.getTo().getAbbr();
        } else if (object instanceof Node) {
            return ((Node)object).getName();
        } else if (object instanceof TimetableImage) {
            return ((TimetableImage)object).getFilename();
        } else {
            throw new IllegalArgumentException("Not known class: " + object.getClass());
        }
    }

    private void addDescription(GTEvent<?> event) {
        String desc = converter.getDesc(event.getType());
        AttributeChange aC = null;
        switch (event.getType()) {
            case ATTRIBUTE:
                // TODO transformation of attribute name? transformation table?
                aC = event.getAttributeChange();
                change.addDescription(new DiagramChangeDescription(desc, aC.getName()));
                break;
            case TRACK_ATTRIBUTE:
                aC = event.getAttributeChange();
                RouteSegmentEvent<?,Track> rse = (RouteSegmentEvent<?, Track>)event;
                change.addDescription(new DiagramChangeDescription(desc, aC.getName(), rse.getTrack().getNumber()));
                break;
            case TECHNOLOGICAL:
                change.addDescription(new DiagramChangeDescription(desc));
                break;
        }
    }
}
