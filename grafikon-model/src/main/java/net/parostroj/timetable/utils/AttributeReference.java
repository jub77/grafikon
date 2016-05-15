package net.parostroj.timetable.utils;

import net.parostroj.timetable.model.AttributesHolder;

/**
 * Reference to attribute.
 *
 * @author jub
 */
public interface AttributeReference<T> extends Reference<T> {

    AttributesHolder getHolder();

    String getCategory();

    String getName();

    Class<T> getClazz();

    T remove();

    static <V> AttributeReference<V> create(AttributesHolder holder, String name, Class<V> clazz) {
        return new AttributeReferenceImpl<>(holder, name, clazz);
    }

    static <V> AttributeReference<V> create(AttributesHolder holder, String category, String name, Class<V> clazz) {
        return new AttributeReferenceImpl<>(holder, category, name, clazz);
    }
}
