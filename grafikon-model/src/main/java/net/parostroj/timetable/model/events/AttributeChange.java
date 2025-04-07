package net.parostroj.timetable.model.events;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

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
        this.oldValue = createCopyIfNeeded(oldValue);
        this.newValue = createCopyIfNeeded(newValue);
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
        for (String aName : names) {
            if (this.name.equals(aName)) {
                return true;
            }
        }
        return false;
    }

    private Object createCopyIfNeeded(Object value) {
        return switch (value) {
            case Set<?> set -> ImmutableSet.copyOf(set);
            case List<?> list -> ImmutableList.copyOf(list);
            case Map<?, ?> map -> ImmutableMap.copyOf(map);
            case null, default -> value;
        };
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("[");
        b.append(name);
        if (category != null) {
            b.append(',').append(category);
        }
        b.append(';');
        b.append(oldValue);
        b.append("->");
        b.append(newValue);
        b.append(']');
        return b.toString();
    }
}
