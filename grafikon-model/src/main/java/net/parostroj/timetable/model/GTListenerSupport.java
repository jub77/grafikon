package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Listener support. Acts as a delegate for distribution of events.
 * 
 * @author jub
 */
class GTListenerSupport<T extends GTListener, E extends GTEvent> {

    private Set<T> listeners;
    private GTEventSender<T, E> sender;

    public GTListenerSupport(GTEventSender<T, E> sender) {
        this.listeners = new HashSet<T>();
        this.sender = sender;
    }

    public void addListener(T listener) {
        this.listeners.add(listener);
    }

    public void removeListener(T listener) {
        this.listeners.remove(listener);
    }

    public void fireEvent(E event) {
        for (T listener : listeners) {
            sender.fireEvent(listener, event);
        }
    }
}
