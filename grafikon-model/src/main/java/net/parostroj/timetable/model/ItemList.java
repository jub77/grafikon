package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public interface ItemList<T> extends Iterable<T> {

    void addAll(Iterable<? extends T> list);

    void replaceAll(Collection<? extends T> list);

    void add(T item);

    void add(T item, int index);

    void remove(T item);

    void move(T item, int index);

    void move(int oldIndex, int newIndex);

    List<T> toList();

    T get(int index);

    int indexOf(T item);

    T[] toArray(T[] array);

    int size();

    boolean isEmpty();

    T find(Predicate<T> predicate);

    Iterator<T> iterator();

}