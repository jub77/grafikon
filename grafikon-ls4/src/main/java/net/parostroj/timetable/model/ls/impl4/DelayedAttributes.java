package net.parostroj.timetable.model.ls.impl4;

import java.util.function.Function;

import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.ls.LSException;

public class DelayedAttributes<T extends AttributesHolder> {

    private final T object;
    private final LSAttributes attributes;
    private final Function<String, ObjectWithId> mapping;

    public DelayedAttributes(T object, LSAttributes attributes) {
        this(object, attributes, null);
    }

    public DelayedAttributes(T object, LSAttributes attributes, Function<String, ObjectWithId> mapping) {
        this.object = object;
        this.attributes = attributes;
        this.mapping = mapping;
    }

    public T getObject() {
        return object;
    }

    public void addAttributes() throws LSException {
        object.getAttributes().add(attributes.createAttributes(mapping));
    }
}
