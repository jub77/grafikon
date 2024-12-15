package net.parostroj.timetable.model.ls.impl4;

import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Attributes with delayed creation in order to get all {@link ObjectWithId} references.
 *
 * @author jub
 */
public class DelayedAttributes<T extends AttributesHolder> {

    private final T object;
    private final LSAttributes attributes;

    public DelayedAttributes(T object, LSAttributes attributes) {
        this.object = object;
        this.attributes = attributes;
    }

    public T getObject() {
        return object;
    }

    public void addAttributes(LSContext context) throws LSException {
        object.getAttributes().add(attributes.createAttributes(context));
    }
}
