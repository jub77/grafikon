package net.parostroj.timetable.model;

import java.util.List;

public interface ItemList<T> extends ItemSet<T> {

    void add(T item, int index);

    void move(T item, int index);

    void move(int oldIndex, int newIndex);

    List<T> toList();

    T get(int index);

    int indexOf(T item);
}
