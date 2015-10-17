package net.parostroj.timetable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Iterables;

public interface ItemSet<T> extends Set<T> {

    default void replaceAll(Collection<? extends T> list) {
        // add missing
        for (T item : list) {
            if (!this.contains(item)) {
                this.add(item);
            }
        }
        // remove deleted
        for (T item : new ArrayList<>(this)) {
            if (!list.contains(item)) {
                this.remove(item);
            }
        }
    }

    default T find(Predicate<T> predicate) {
        return Iterables.tryFind(this, predicate::test).orNull();
    }
}
