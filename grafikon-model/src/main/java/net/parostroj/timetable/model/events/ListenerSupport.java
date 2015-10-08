package net.parostroj.timetable.model.events;

import java.util.HashSet;
import java.util.Set;

/**
 * Listener support. Acts as a delegate for distribution of events.
 *
 * @author jub
 */
public class ListenerSupport {

    private final Set<Listener> listeners;

    public ListenerSupport() {
        this.listeners = new HashSet<>();
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    public void fireEvent(Event event) {
        for (Listener listener : listeners) {
            listener.changed(event);
        }
    }

    @Override
    public String toString() {
        return String.format("Listener support: %d listeners", listeners.size());
    }
}
