package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Train diagram event.
 * 
 * @author jub
 */
public class TrainDiagramEvent extends GTEvent<TrainDiagram> {

    private Object object;

    public TrainDiagramEvent(TrainDiagram diagram, GTEventType type) {
        super(diagram, type);
    }
    
    public TrainDiagramEvent(TrainDiagram diagram, GTEvent<?> event) {
        super(diagram, event);
    }

    public TrainDiagramEvent(TrainDiagram diagram, GTEventType type, Object object) {
        super(diagram, type);
        this.object = object;
    }


    public TrainDiagramEvent(TrainDiagram diagram, AttributeChange attributeChange) {
        super(diagram, GTEventType.ATTRIBUTE);
        setAttributeChange(attributeChange);
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TrainDiagramEvent[");
        builder.append(getSource()).append(',');
        builder.append(getType());
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
