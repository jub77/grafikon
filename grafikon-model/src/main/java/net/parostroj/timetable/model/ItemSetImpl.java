package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.Event;

class ItemSetImpl<T> extends AbstractSet<T> implements ItemSet<T> {

    interface ItemSetEventCallback<E> {
        void fire(Event.Type type, E item);
    }

    private final Set<T> items;
    private final ItemSetEventCallback<T> eventCallback;

    protected ItemSetImpl() {
        this(null);
    }

    protected ItemSetImpl(ItemSetEventCallback<T> eventCallback) {
        this.items = new HashSet<>();
        this.eventCallback = eventCallback;
    }

    @Override
    public boolean add(T item) {
        boolean added = items.add(item);
        if (added) {
            this.fireEvent(Event.Type.ADDED, item);
        }
        return added;
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

            private T last;

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                last = iter.next();
                return last;
            }

            @Override
            public void remove() {
                iter.remove();
                fireEvent(Event.Type.REMOVED, last);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSetImpl<?> itemSet = (ItemSetImpl<?>) o;
        return Objects.equals(items, itemSet.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
