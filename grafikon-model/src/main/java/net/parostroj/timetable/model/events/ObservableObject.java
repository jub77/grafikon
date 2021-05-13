package net.parostroj.timetable.model.events;

/**
 * Observable
 *
 * @author jub
 */
public interface ObservableObject {

    void addListener(Listener listener);

    void removeListener(Listener listener);
}
