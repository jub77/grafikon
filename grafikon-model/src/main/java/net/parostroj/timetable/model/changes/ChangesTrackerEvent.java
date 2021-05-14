package net.parostroj.timetable.model.changes;

/**
 * Event for changes tracker.
 *
 * @author jub
 */
public class ChangesTrackerEvent {

    public enum Type {
        CHANGE_MODIFIED, CHANGE_ADDED, CHANGE_REMOVED, SET_ADDED, SET_REMOVED, CURRENT_SET_CHANGED, TRACKING_ENABLED, TRACKING_DISABLED, SET_MODIFIED
    }

    private final Type type;
    private DiagramChange change;
    private DiagramChangeSet set;

    public ChangesTrackerEvent(Type type) {
        this.type = type;
    }

    public ChangesTrackerEvent(Type type, DiagramChangeSet set, DiagramChange change) {
        this.change = change;
        this.type = type;
        this.set = set;
    }

    public ChangesTrackerEvent(Type type, DiagramChangeSet set) {
        this.type = type;
        this.set = set;
    }

    public DiagramChange getChange() {
        return change;
    }

    public DiagramChangeSet getSet() {
        return set;
    }

    public Type getType() {
        return type;
    }
}
