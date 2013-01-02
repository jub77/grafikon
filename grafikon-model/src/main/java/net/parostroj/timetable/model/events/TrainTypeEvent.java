package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Train type event.
 * 
 * @author jub
 */
public class TrainTypeEvent extends GTEvent<TrainType> {

    public TrainTypeEvent(TrainType trainType, GTEventType type) {
        super(trainType, type);
    }

    public TrainTypeEvent(TrainType trainType, AttributeChange change) {
        super(trainType, GTEventType.ATTRIBUTE);
        this.setAttributeChange(change);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TrainTypeEvent[");
        builder.append(getSource()).append(',');
        builder.append(getType());
        if (getType() == GTEventType.ATTRIBUTE) {
            builder.append(',').append(getAttributeChange());
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }
}
