package net.parostroj.timetable.model;

import com.google.common.collect.Iterables;

public interface ItemWithIdIterable<T extends ObjectWithId> extends Iterable<T> {

    default T getById(String id) {
        return Iterables.tryFind(this, item -> item.getId().equals(id)).orNull();
    }
}
