package net.parostroj.timetable.model;

import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;

public interface ItemList<T> extends List<T> {

    default void move(T item, int index) {
        int oldIndex = this.indexOf(item);
        if (oldIndex == -1) {
            throw new IllegalArgumentException("Item not in list");
        }
        this.move(oldIndex, index);
    }

    void move(int oldIndex, int newIndex);

    default T find(Predicate<T> predicate) {
        return Iterables.tryFind(this, predicate::test).orNull();
    }
}
