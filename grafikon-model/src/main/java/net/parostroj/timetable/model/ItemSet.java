package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

public interface ItemSet<T> extends Set<T> {

    default void replaceAll(Collection<? extends T> list) {
        // remove deleted
        this.retainAll(list);
        // add missing
        this.addAll(list);
    }

    default Optional<T> find(Predicate<T> predicate) {
        return Iterables.tryFind(this, predicate::test).toJavaUtil();
    }

    default Set<T> findAll(Predicate<T> predicate) {
        return FluentIterable.from(this).filter(predicate::test).toSet();
    }
}
