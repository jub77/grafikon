package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.model.events.Listener;
import net.parostroj.timetable.model.events.WeakListener;

/**
 * Listener support. Acts as a delegate for distribution of events.
 *
 * @author jub
 */
class ListenerSupport {

    private final Set<Listener> listeners;
    private final Set<Listener> weakListeners;
    private final Set<Listener> systemListeners;

    public ListenerSupport() {
        this.listeners = new HashSet<>();
        this.systemListeners = new HashSet<>();
        this.weakListeners = Collections.newSetFromMap(new WeakHashMap<>());
    }

    public void addListener(Listener listener) {
        if (listener instanceof SystemListener) {
            this.systemListeners.add(listener);
        } else if (listener instanceof WeakListener) {
            this.weakListeners.add(listener);
        } else {
            this.listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        if (listener instanceof SystemListener) {
            this.systemListeners.remove(listener);
        } else if (listener instanceof WeakListener) {
            this.weakListeners.remove(listener);
        } else {
            this.listeners.remove(listener);
        }
    }

    public void removeAllListeners() {
        this.systemListeners.clear();
        this.weakListeners.clear();
        this.listeners.clear();
    }

    public void fireEvent(Event event) {
        for (Listener listener : systemListeners) {
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
