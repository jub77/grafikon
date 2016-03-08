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

    default <T> T getAttribute(String name, Class<T> clazz, T defaultValue) {
        return getAttributes().get(name, clazz, defaultValue);
    }

    default void setAttribute(String name, Object value) {
        getAttributes().set(name, value);
    }

    default void setRemoveAttribute(String name, Object value) {
        getAttributes().setRemove(name, value);
    }

    default Object removeAttribute(String name) {
        return getAttributes().remove(name);
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

    default void setAttributeAsBool(String name, boolean value) {
        getAttributes().setBool(name, value);
    }

    Attributes getAttributes();
}
