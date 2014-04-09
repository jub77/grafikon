package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.GTEventType;

public class ItemList<T> {

    protected enum Type {ADD, REMOVE, MOVE};

    private final List<T> items;
    private final Map<Type, GTEventType> events = new EnumMap<Type, GTEventType>(Type.class);

    protected ItemList(GTEventType add, GTEventType remove, GTEventType move) {
        events.put(Type.ADD, add);
        events.put(Type.REMOVE, remove);
        events.put(Type.MOVE, move);
        items = new ArrayList<T>();
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
        int oldIndex = items.indexOf(item);
        if (oldIndex == -1) {
            throw new IllegalArgumentException("Item not in list.");
        }
        this.move(oldIndex, index);

    }

    public void move(int oldIndex, int newIndex) {
        T item = items.remove(oldIndex);
        items.add(newIndex, item);
        this.fireEvent(Type.MOVE, item, newIndex, oldIndex);
    }

    public List<T> get() {
        return Collections.unmodifiableList(items);
    }

    private void fireEvent(Type type, T item, int newIndex, int oldIndex) {
        this.fireEvent(type, getType(type), item, newIndex, oldIndex);
    }

    protected GTEventType getType(Type type) {
        return events.get(type);
    }

    protected void fireEvent(Type type, GTEventType eventType, T item, int newIndex, int oldIndex) {
    }
}
