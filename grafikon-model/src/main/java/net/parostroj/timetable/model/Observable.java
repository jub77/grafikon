package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.Listener;

/**
 * Observable
 *
 * @author jub
 */
public interface Observable {

    void addListener(Listener listener);

    void removeListener(Listener listener);
}
