package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;

public interface ItemSet<T> extends Iterable<T> {

    default void addAll(Iterable<? extends T> list) {
        for (T item : list) {
            this.add(item);
        }
    }

    default void replaceAll(Collection<? extends T> list) {
        // add missing
        for (T item : list) {
            if (!this.contains(item)) {
                this.add(item);
            }
        }
        // remove deleted
        for (T item : new ArrayList<>(toCollection())) {
            if (!list.contains(item)) {
                this.remove(item);
            }
        }
    }

    void add(T item);

    void remove(T item);

    boolean contains(T item);

    Collection<T> toCollection();

    default T[] toArray(T[] array) {
        return toCollection().toArray(array);
    }

    int size();

    default boolean isEmpty() {
        return this.size() == 0;
    }

    default T find(Predicate<T> predicate) {
        return Iterables.tryFind(this, predicate::test).orNull();
    }
}
