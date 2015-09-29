package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.GTEventType;

public class ItemList<T> implements Iterable<T> {

    protected enum Type {ADD, REMOVE, MOVE};

    private final List<T> items;
    private final Map<Type, GTEventType> events = new EnumMap<Type, GTEventType>(Type.class);
    private final boolean moveAllowed;

    protected ItemList(GTEventType add, GTEventType remove, GTEventType move) {
        events.put(Type.ADD, add);
        events.put(Type.REMOVE, remove);
        events.put(Type.MOVE, move);
        items = new ArrayList<T>();
        moveAllowed = true;
    }

    protected ItemList(GTEventType add, GTEventType remove) {
        events.put(Type.ADD, add);
        events.put(Type.REMOVE, remove);
        items = new ArrayList<T>();
        moveAllowed = false;
    }

    public void addAll(Iterable<? extends T> list) {
        for (T item : list) {
            this.add(item);
        }
    }

    public void mergeImmutable(Collection<? extends T> list) {
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

    public void add(T item) {
        items.add(item);
        this.fireEvent(Type.ADD, item, items.size() - 1, 0);
    }

    public void add(T item, int index) {
        items.add(index, item);
        this.fireEvent(Type.ADD, item, index, 0);
    }

    public void remove(T item) {
        if (items.remove(item)) {
            this.fireEvent(Type.REMOVE, item, 0, 0);
        }
    }

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

    public void move(int oldIndex, int newIndex) {
        if (!moveAllowed) {
            throw new IllegalStateException("Move not allowed");
        }
        T item = items.remove(oldIndex);
        items.add(newIndex, item);
        this.fireEvent(Type.MOVE, item, newIndex, oldIndex);
    }

    public List<T> toList() {
        return Collections.unmodifiableList(items);
    }

    public T get(int index) {
        return items.get(index);
    }

    public int indexOf(T item) {
        return items.indexOf(item);
    }

    public T[] toArray(T[] array) {
        return items.toArray(array);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    private void fireEvent(Type type, T item, int newIndex, int oldIndex) {
        this.fireEvent(type, getType(type), item, newIndex, oldIndex);
    }

    protected GTEventType getType(Type type) {
        return events.get(type);
    }

    protected void fireEvent(Type type, GTEventType eventType, T item, int newIndex, int oldIndex) {
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
