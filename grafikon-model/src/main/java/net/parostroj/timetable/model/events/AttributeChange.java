package net.parostroj.timetable.model.events;

/**
 * The information about attribute value change.
 *
 * @author jub
 */
public class AttributeChange {

    private final String name;
    private final Object oldValue;
    private final Object newValue;
    private final String category;

    public AttributeChange(String name, Object oldValue, Object newValue) {
        this(name, oldValue, newValue, null);
    }

    public AttributeChange(String name, Object oldValue, Object newValue, String category) {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public String getCategory() {
        return category;
    }

    public boolean checkName(String... names) {
        for (String name : names) {
            if (this.name.equals(name)) {
                return true;
            }
        }
        return false;
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
