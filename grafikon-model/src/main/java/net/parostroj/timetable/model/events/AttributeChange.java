package net.parostroj.timetable.model.events;

/**
 * The information about attribute value change.
 *
 * @author jub
 */
public class AttributeChange {

    private String name;
    private Object oldValue;
    private Object newValue;

    public AttributeChange(String name, Object oldValue, Object newValue) {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public AttributeChange() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder('[');
        b.append(name);
        b.append(';');
        b.append(oldValue);
        b.append("->");
        b.append(newValue);
        b.append(']');
        return b.toString();
    }
}
