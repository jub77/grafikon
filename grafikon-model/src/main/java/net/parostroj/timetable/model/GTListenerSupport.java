package net.parostroj.timetable.model;

import net.parostroj.timetable.model.events.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Listener support. Acts as a delegate for distribution of events.
 *
 * @author jub
 */
class GTListenerSupport<T extends GTListener, E extends GTEvent<?>> {

    private final Set<T> listeners;
    private final GTEventSender<T, E> sender;

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

    public void removeAllListeners() {
        this.listeners.clear();
    }

    public void fireEvent(E event) {
        for (T listener : listeners) {
            sender.fireEvent(listener, event);
        }
    }

    @Override
    public String toString() {
        return String.format("Listener support: %d listeners", listeners.size());
    }
}
