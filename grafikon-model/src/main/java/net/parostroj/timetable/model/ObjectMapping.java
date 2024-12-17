package net.parostroj.timetable.model;

import java.util.function.Function;

public interface ObjectMapping<T extends ObjectWithId> {
    T getObject(String id);

    static <O extends ObjectWithId> ObjectMapping<O> fromFunction(Function<String, O> func) {
        return func::apply;
    }
}
