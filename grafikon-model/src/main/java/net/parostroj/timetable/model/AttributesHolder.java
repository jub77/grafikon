package net.parostroj.timetable.model;

/**
 * Interface for class that hold attributes.
 *
 * @author jub
 */
public interface AttributesHolder {

    public Object getAttribute(String key);

    public void setAttribute(String key, Object value);

    public Object removeAttribute(String key);
    
    public Attributes getAttributes();
    
    public void setAttributes(Attributes attributes);
}
