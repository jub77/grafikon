package net.parostroj.timetable.model;

/**
 * Attribute reference with type.
 *
 * @author jub
 */
public class TypedAttributeReference<T>  extends AttributeReference {

    private Class<T> clazz;

    public TypedAttributeReference(AttributesHolder holder, String category, String name, Class<T> clazz) {
        super(holder, category, name);
        this.clazz = clazz;
    }

    public T get() {
        return this.get(clazz);
    }
}
