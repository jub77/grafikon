package net.parostroj.timetable.model;

import java.util.*;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;

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
    public void addAll(Iterable<? extends T> list) {
        for (T item : list) {
            this.add(item);
        }
    }

    @Override
    public void replaceAll(Collection<? extends T> list) {
        // add missing
        for (T item : list) {
            if (!items.contains(item)) {
                this.add(item);
            }
        }
        // remove deleted
        for (T item : new ArrayList<>(items)) {
            if (!list.contains(item)) {
                this.remove(item);
            }
        }
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
    public T get(int index) {
        return items.get(index);
    }

    @Override
    public int indexOf(T item) {
        return items.indexOf(item);
    }

    @Override
    public T[] toArray(T[] array) {
        return items.toArray(array);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public T find(Predicate<T> predicate) {
        return Iterables.tryFind(items, predicate::test).orNull();
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
