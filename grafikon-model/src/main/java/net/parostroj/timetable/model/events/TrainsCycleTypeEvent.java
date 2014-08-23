package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Trains cycle type event.
 *
 * @author jub
 */
public class TrainsCycleTypeEvent extends GTEvent<TrainsCycleType> {

    public TrainsCycleTypeEvent(TrainsCycleType cycleType, GTEventType type) {
        super(cycleType, type);
    }

    public TrainsCycleTypeEvent(TrainsCycleType cycleType, AttributeChange attributeChange) {
        super(cycleType, GTEventType.ATTRIBUTE);
        this.setAttributeChange(attributeChange);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TrainsCycleTypeEvent[");
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
