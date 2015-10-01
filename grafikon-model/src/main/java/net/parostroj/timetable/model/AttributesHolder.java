package net.parostroj.timetable.model;

/**
 * Interface for class that hold attributes.
 *
 * @author jub
 */
public interface AttributesHolder {

    public <T> T getAttribute(String key, Class<T> clazz);

    public void setAttribute(String key, Object value);

    public Object removeAttribute(String key);

    public Attributes getAttributes();
}
