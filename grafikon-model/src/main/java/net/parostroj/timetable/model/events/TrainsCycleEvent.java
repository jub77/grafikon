package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Trains cycle event.
 *
 * @author jub
 */
public class TrainsCycleEvent extends GTEvent<TrainsCycle> {

    private TrainsCycleItem oldCycleItem;
    private TrainsCycleItem newCycleItem;

    public TrainsCycleEvent(TrainsCycle cycle, GTEventType type) {
        super(cycle, type);
    }

    public TrainsCycleEvent(TrainsCycle cycle, GTEventType type, TrainsCycleItem oldCycleItem, TrainsCycleItem newCycleItem) {
        super(cycle, type);
        this.oldCycleItem = oldCycleItem;
        this.newCycleItem = newCycleItem;
    }

    public TrainsCycleEvent(TrainsCycle cycle, AttributeChange attributeChange) {
        super(cycle, GTEventType.ATTRIBUTE);
        this.setAttributeChange(attributeChange);
    }

    public TrainsCycleItem getNewCycleItem() {
        return newCycleItem;
    }

    public TrainsCycleItem getOldCycleItem() {
        return oldCycleItem;
    }

    public void setNewCycleItem(TrainsCycleItem newCycleItem) {
        this.newCycleItem = newCycleItem;
    }

    public void setOldCycleItem(TrainsCycleItem oldCycleItem) {
        this.oldCycleItem = oldCycleItem;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TrainsCycleEvent[");
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
