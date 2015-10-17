package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.Event;

class ItemListImpl<T> implements ItemList<T> {

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
    public void add(T item) {
        items.add(item);
        this.fireEvent(Event.Type.ADDED, item, items.size() - 1, null);
    }

    @Override
    public void add(int index, T item) {
        items.add(index, item);
        this.fireEvent(Event.Type.ADDED, item, index, null);
    }

    @Override
    public void remove(T item) {
        int index = items.indexOf(item);
        if (items.remove(item)) {
            this.fireEvent(Event.Type.REMOVED, item, index, null);
        }
    }

    @Override
    public void move(T item, int index) {
        int oldIndex = items.indexOf(item);
        if (oldIndex == -1) {
            throw new IllegalArgumentException("Item not in list");
        }
        this.move(oldIndex, index);

    }

    @Override
    public void move(int oldIndex, int newIndex) {
        T item = items.remove(oldIndex);
        items.add(newIndex, item);
        this.fireEvent(Event.Type.MOVED, item, newIndex, oldIndex);
    }

    @Override
    public List<T> toList() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public Collection<T> toCollection() {
        return this.toList();
    }

    @Override
    public T get(int index) {
        return items.get(index);
    }

    @Override
    public int indexOf(T item) {
        return items.indexOf(item);
    }

    @Override
    public boolean contains(T item) {
        return items.contains(item);
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
        return items.iterator();
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
