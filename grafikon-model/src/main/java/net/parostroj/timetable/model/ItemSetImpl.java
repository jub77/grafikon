package net.parostroj.timetable.model;

import java.util.*;

import net.parostroj.timetable.model.events.Event;

class ItemSetImpl<T> implements ItemSet<T> {

    private final List<T> items;

    protected ItemSetImpl() {
        this.items = new ArrayList<>();
    }

    @Override
    public void add(T item) {
        items.add(item);
        this.fireEvent(Event.Type.ADDED, item);
    }

    @Override
    public void remove(T item) {
        if (items.remove(item)) {
            this.fireEvent(Event.Type.REMOVED, item);
        }
    }

    @Override
    public Collection<T> toCollection() {
        return Collections.unmodifiableCollection(items);
    }

    @Override
    public boolean contains(T item) {
        return items.contains(item);
    }

    @Override
    public int size() {
        return items.size();
    }

    protected void fireEvent(Event.Type type, T item) {
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
