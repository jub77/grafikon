package net.parostroj.timetable.gui.views;

import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.TimeConverter;

/**
 * Wrapper for trains cycle item.
 *
 * @author jub
 */
public class TrainsCycleItemWrapper {
    
    private TrainsCycleItem item;

    public TrainsCycleItemWrapper(TrainsCycleItem item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return String.format("%s (%s[%s],%s[%s])", item.getTrain().getName(),item.getFromInterval().getOwnerAsNode().getName(),TimeConverter.convertFromIntToText(item.getStartTime()),item.getToInterval().getOwnerAsNode().getName(),TimeConverter.convertFromIntToText(item.getEndTime()));
    }

    public TrainsCycleItem getItem() {
        return item;
    }

    public void setItem(TrainsCycleItem item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TrainsCycleItemWrapper other = (TrainsCycleItemWrapper) obj;
        if (this.item != other.item && (this.item == null || !this.item.equals(other.item))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.item != null ? this.item.hashCode() : 0);
        return hash;
    }
}
