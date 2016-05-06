package net.parostroj.timetable.model;

/**
 * Reference to attribute.
 *
 * @author jub
 */
public class AttributeReference {

    private final AttributesHolder holder;
    private final String category;
    private final String name;

    public AttributeReference(AttributesHolder holder, String name) {
        this(holder, null, name);
    }

    public AttributeReference(AttributesHolder holder, String category, String name) {
        this.holder = holder;
        this.category = category;
        this.name = name;
    }

    public AttributesHolder getHolder() {
        return holder;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void set(Object value) {
        holder.getAttributes().set(name, value, category);
    }

    public <T> T get(Class<T> clazz) {
        return holder.getAttributes().get(name, category, clazz);
    }
}
