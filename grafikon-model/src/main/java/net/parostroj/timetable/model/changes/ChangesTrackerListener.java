package net.parostroj.timetable.model.changes;

import net.parostroj.timetable.model.changes.DiagramChange;

/**
 * Listener.
 *
 * @author jub
 */
public interface ChangesTrackerListener {
    public void changeReceived(DiagramChange change);
}
