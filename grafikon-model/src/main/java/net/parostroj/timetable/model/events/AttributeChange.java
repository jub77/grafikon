package net.parostroj.timetable.model.events;

import java.util.Collection;
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
        for (String name : names) {
            if (this.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private Object createCopyIfNeeded(Object value) {
        if (value instanceof Set) {
            return ImmutableSet.copyOf((Collection<?>) value);
        } else if (value instanceof List) {
            return ImmutableList.copyOf((Iterable<?>) value);
        } else if (value instanceof Map) {
            return ImmutableMap.copyOf((Map<?, ?>) value);
        } else {
            return value;
        }
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
