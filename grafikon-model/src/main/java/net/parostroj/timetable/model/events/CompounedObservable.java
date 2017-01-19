package net.parostroj.timetable.model.events;

/**
 * Observable for observing events of nested objects.
 *
 * @author jub
 */
public interface CompounedObservable {

    void addAllEventListener(Listener listener);

    void removeAllEventListener(Listener listener);
}
