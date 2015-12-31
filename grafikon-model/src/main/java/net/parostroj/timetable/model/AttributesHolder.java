package net.parostroj.timetable.model;

/**
 * Interface for class that hold attributes.
 *
 * @author jub
 */
public interface AttributesHolder {

    default <T> T getAttribute(String key, Class<T> clazz) {
        return getAttributes().get(key, clazz);
    }

    default void setAttribute(String key, Object value) {
        getAttributes().set(key, value);
    }

    default Object removeAttribute(String key) {
        return getAttributes().remove(key);
    }

    Attributes getAttributes();
}
