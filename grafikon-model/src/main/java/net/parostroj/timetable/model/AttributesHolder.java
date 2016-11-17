package net.parostroj.timetable.model;

import java.util.Collection;
import java.util.List;

/**
 * Interface for class that hold attributes.
 *
 * @author jub
 */
public interface AttributesHolder {

    default <T> T getAttribute(String name, Class<T> clazz) {
        return getAttributes().get(name, clazz);
    }

    default <T> T getAttribute(String category, String name, Class<T> clazz) {
        return getAttributes().get(category, name, clazz);
    }

    default <T> T getAttribute(String name, Class<T> clazz, T defaultValue) {
        return getAttributes().get(name, clazz, defaultValue);
    }

    default <T> T getAttribute(String category, String name, Class<T> clazz, T defaultValue) {
        return getAttributes().get(category, name, clazz, defaultValue);
    }

    default void setAttribute(String name, Object value) {
        getAttributes().set(name, value);
    }

    default void setAttribute(String category, String name, Object value) {
        getAttributes().set(category, name, value);
    }

    default void setRemoveAttribute(String name, Object value) {
        getAttributes().setRemove(name, value);
    }

    default void setRemoveAttribute(String category, String name, Object value) {
        getAttributes().setRemove(category, name, value);
    }

    default Object removeAttribute(String name) {
        return getAttributes().remove(name);
    }

    default Object removeAttribute(String category, String name) {
        return getAttributes().remove(category, name);
    }

    default <T> Collection<T> getAttributeAsCollection(String name, Class<T> clazz) {
        return getAttributes().getAsCollection(name, clazz);
    }

    default <T> Collection<T> getAttributeAsCollection(String name, Class<T> clazz, Collection<T> defaultValue) {
        return getAttributes().getAsCollection(name, clazz, defaultValue);
    }

    default <T> List<T> getAttributeAsList(String name, Class<T> clazz) {
        return getAttributes().getAsList(name, clazz);
    }

    default <T> List<T> getAttributeAsList(String name, Class<T> clazz, List<T> defaultValue) {
        return getAttributes().getAsList(name, clazz, defaultValue);
    }

    default boolean getAttributeAsBool(String name) {
        return getAttributes().getBool(name);
    }

    default boolean getAttributeAsBool(String category, String name) {
        return getAttributes().getBool(category, name);
    }

    default void setAttributeAsBool(String name, boolean value) {
        getAttributes().setBool(name, value);
    }

    default void setAttributeAsBool(String category, String name, boolean value) {
        getAttributes().setBool(category, name, value);
    }

    default <T> T getNestedAttribute(Class<T> clazz, String... names) {
        return clazz.cast(getNestedAttribute(names));
    }

    default Object getNestedAttribute(String... names) {
        if (names.length == 0) {
            return 0;
        }
        Object current = this;
        for (String name : names) {
            if (!(current instanceof AttributesHolder)) {
                return null;
            }
            AttributesHolder holder = (AttributesHolder) current;
            current = holder.getAttribute(name, Object.class);
        }
        return current;
    }

    Attributes getAttributes();
}
