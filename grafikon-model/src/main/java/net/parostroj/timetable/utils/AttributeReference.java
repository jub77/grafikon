package net.parostroj.timetable.utils;

import com.google.common.base.Objects;

import net.parostroj.timetable.model.AttributesHolder;

/**
 * Reference to attribute.
 *
 * @author jub
 */
public class AttributeReference<T> implements Reference<T> {

    private final AttributesHolder holder;
    private final String category;
    private final String name;
    private final Class<T> clazz;

    public AttributeReference(AttributesHolder holder, String name, Class<T> clazz) {
        this(holder, null, name, clazz);
    }

    public AttributeReference(AttributesHolder holder, String category, String name, Class<T> clazz) {
        this.holder = holder;
        this.category = category;
        this.name = name;
        this.clazz = clazz;
    }

    public AttributesHolder getHolder() {
        return holder;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    @Override
    public void set(T value) {
        holder.getAttributes().set(name, value, category);
    }

    @Override
    public T get() {
        return holder.getAttributes().get(name, category, clazz);
    }

    public boolean remove() {
        return holder.getAttributes().remove(name, category) != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AttributeReference)) return false;
        AttributeReference<?> ref = (AttributeReference<?>) obj;
        return Objects.equal(category, ref.category) && Objects.equal(name, ref.name) && holder.equals(ref.holder);
    }
}
