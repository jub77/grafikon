package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.Event;

class ItemListImpl<T> extends AbstractList<T> implements ItemList<T> {

    interface ItemListEventCallback<E> {
        void fire(Event.Type type, E item, Integer newIndex, Integer oldIndex);
    }

    private final List<T> items;
    private final ItemListEventCallback<T> eventCallback;

    protected ItemListImpl() {
        this(null);
    }

    protected ItemListImpl(ItemListEventCallback<T> eventCallback) {
        this.items = new ArrayList<>();
        this.eventCallback = eventCallback;
    }

    @Override
    public void add(int index, T item) {
        items.add(index, item);
        this.fireEvent(Event.Type.ADDED, item, index, null);
    }

    @Override
    public T remove(int index) {
        T removed = items.remove(index);
        if (removed != null) {
            this.fireEvent(Event.Type.REMOVED, removed, index, null);
        }
        return removed;
    }

    @Override
    public void move(int oldIndex, int newIndex) {
        T item = items.remove(oldIndex);
        items.add(newIndex, item);
        this.fireEvent(Event.Type.MOVED, item, newIndex, oldIndex);
    }

    @Override
    public T get(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

    private void fireEvent(Event.Type type, T item, Integer newIndex, Integer oldIndex) {
        if (eventCallback != null) {
            eventCallback.fire(type, item, newIndex, oldIndex);
        }
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> iter = items.iterator();
        return new Iterator<>() {

            private T last;
            private int index = -1;

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                last = iter.next();
                index++;
                return last;
            }

            @Override
            public void remove() {
                iter.remove();
                fireEvent(Event.Type.REMOVED, last, index, null);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemListImpl<?> itemList = (ItemListImpl<?>) o;
        return Objects.equals(items, itemList.items);
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
