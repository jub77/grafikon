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
    private String category;

    public AttributeChange(String name, Object oldValue, Object newValue) {
        this(name, oldValue, newValue, null);
    }

    public AttributeChange(String name, Object oldValue, Object newValue, String category) {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder('[');
        b.append(name);
        if (category != null)
            b.append(',').append(category);
        b.append(';');
        b.append(oldValue);
        b.append("->");
        b.append(newValue);
        b.append(']');
        return b.toString();
    }
}
