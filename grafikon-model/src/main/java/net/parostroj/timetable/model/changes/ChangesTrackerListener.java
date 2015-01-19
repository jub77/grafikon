package net.parostroj.timetable.model.changes;

/**
 * Listener.
 *
 * @author jub
 */
public interface ChangesTrackerListener {
    public void trackerChanged(ChangesTrackerEvent event);
}
