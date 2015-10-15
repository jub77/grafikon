package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.Event;

class ItemListImpl<T> implements ItemList<T> {

    private final List<T> items;
    private final boolean moveAllowed;

    protected ItemListImpl(boolean moveAllowed) {
        this.items = new ArrayList<>();
        this.moveAllowed = moveAllowed;
    }

    protected ItemListImpl() {
        this(false);
    }


    @Override
    public void add(T item) {
        items.add(item);
        this.fireEvent(Event.Type.ADDED, item, items.size() - 1, null);
    }

    @Override
    public void add(T item, int index) {
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
        if (!moveAllowed) {
            throw new IllegalStateException("Move not allowed");
        }
        int oldIndex = items.indexOf(item);
        if (oldIndex == -1) {
            throw new IllegalArgumentException("Item not in list");
        }
        this.move(oldIndex, index);

    }

    @Override
    public void move(int oldIndex, int newIndex) {
        if (!moveAllowed) {
            throw new IllegalStateException("Move not allowed");
        }
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

    protected void fireEvent(Event.Type type, T item, Integer newIndex, Integer oldIndex) {
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
