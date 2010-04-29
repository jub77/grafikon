package net.parostroj.timetable.net;

/**
 * Listener.
 *
 * @author jub
 */
public interface ChangesTrackerListener {
    public void changeReceived(DiagramChange change);
}
