package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.Event;

class ItemSetImpl<T> extends AbstractSet<T> implements ItemSet<T> {

    interface ItemSetEventCallback<E> {
        void fire(Event.Type type, E item);
    }

    private final List<T> items;
    private final ItemSetEventCallback<T> eventCallback;

    protected ItemSetImpl() {
        this(null);
    }

    protected ItemSetImpl(ItemSetEventCallback<T> eventCallback) {
        this.items = new ArrayList<>();
        this.eventCallback = eventCallback;
    }

    @Override
    public boolean add(T item) {
        items.add(item);
        this.fireEvent(Event.Type.ADDED, item);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object item) {
        boolean removed = items.remove(item);
        if (removed) {
            this.fireEvent(Event.Type.REMOVED, (T)item);
        }
        return removed;
    }

    @Override
    public int size() {
        return items.size();
    }

    private void fireEvent(Event.Type type, T item) {
        if (eventCallback != null) {
            eventCallback.fire(type, item);
        }
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> iter = items.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                return iter.next();
            }
        };
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
