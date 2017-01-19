package net.parostroj.timetable.model.events;

/**
 * Observable
 *
 * @author jub
 */
public interface Observable {

    void addListener(Listener listener);

    void removeListener(Listener listener);
}
