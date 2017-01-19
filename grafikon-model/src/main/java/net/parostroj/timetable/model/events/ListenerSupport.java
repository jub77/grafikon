package net.parostroj.timetable.model.events;

import java.util.*;

/**
 * Listener support. Acts as a delegate for distribution of events.
 *
 * @author jub
 */
public class ListenerSupport {

    private final Set<Listener> listeners;
    private final Set<Listener> weakListeners;
    private final Set<Listener> validationListeners;

    public ListenerSupport() {
        this.listeners = new HashSet<>();
        this.validationListeners = new HashSet<>();
        this.weakListeners = Collections.newSetFromMap(new WeakHashMap<>());
    }

    public void addListener(Listener listener) {
        if (listener instanceof ValidationListener) {
            this.validationListeners.add(listener);
        } else if (listener instanceof WeakListener) {
            this.weakListeners.add(listener);
        } else {
            this.listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        if (listener instanceof ValidationListener) {
            this.validationListeners.remove(listener);
        } else if (listener instanceof WeakListener) {
            this.weakListeners.remove(listener);
        } else {
            this.listeners.remove(listener);
        }
    }

    public void removeAllListeners() {
        this.validationListeners.clear();
        this.weakListeners.clear();
        this.listeners.clear();
    }

    public void fireEvent(Event event) {
        for (Listener listener : validationListeners) {
            listener.changed(event);
        }
        for (Listener listener : listeners) {
            listener.changed(event);
        }
        for (Listener listener : weakListeners) {
            listener.changed(event);
        }
    }

    @Override
    public String toString() {
        return String.format("Listener support: %d listeners", listeners.size() + weakListeners.size());
    }
}
