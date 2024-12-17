package net.parostroj.timetable.model.changes;

/**
 * Listener.
 *
 * @author jub
 */
public interface ChangesTrackerListener {
    void trackerChanged(ChangesTrackerEvent event);
}
