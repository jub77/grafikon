package net.parostroj.timetable.utils;

import com.google.common.base.Objects;

import net.parostroj.timetable.model.AttributesHolder;

/**
 * Reference to attribute.
 *
 * @author jub
 */
class AttributeReferenceImpl<T> implements AttributeReference<T> {

    private final AttributesHolder holder;
    private final String category;
    private final String name;
    private final Class<T> clazz;

    public AttributeReferenceImpl(AttributesHolder holder, String name, Class<T> clazz) {
        this(holder, null, name, clazz);
    }

    public AttributeReferenceImpl(AttributesHolder holder, String category, String name, Class<T> clazz) {
        this.holder = holder;
        this.category = category;
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public AttributesHolder getHolder() {
        return holder;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getClazz() {
        return clazz;
    }

    @Override
    public void set(T value) {
        holder.getAttributes().set(category, name, value);
    }

    @Override
    public T get() {
        return holder.getAttributes().get(category, name, clazz);
    }

    @Override
    public T remove() {
        return clazz.cast(holder.getAttributes().remove(category, name));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AttributeReferenceImpl)) return false;
        AttributeReferenceImpl<?> ref = (AttributeReferenceImpl<?>) obj;
        return Objects.equal(category, ref.category) && Objects.equal(name, ref.name) && holder.equals(ref.holder);
    }
}
