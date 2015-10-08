package net.parostroj.timetable.model.events;

/**
 * @author jub
 */
public class SpecialTrainTimeIntervalList {
    public enum Type { MOVED, STOP_TIME, SPEED, TRACK, RECALCULATE, ADDED }

    private final Type type;
    private final int start;
    private final int changed;

    public SpecialTrainTimeIntervalList(Type type, int start, int changed) {
        this.type = type;
        this.start = start;
        this.changed = changed;
    }

    public Type getType() {
        return type;
    }

    public int getStart() {
        return start;
    }

    public int getChanged() {
        return changed;
    }
}
