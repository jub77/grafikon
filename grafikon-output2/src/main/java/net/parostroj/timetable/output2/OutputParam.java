package net.parostroj.timetable.output2;

/**
 * Output parameter.
 *
 * @author jub
 */
public class OutputParam {

    private final String name;
    private Object value;
    private boolean optional;

    public OutputParam(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public OutputParam(String name, Object value, boolean optional) {
        this(name, value);
        this.optional = optional;
    }

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return optional;
    }

    public Object getValue() {
        return value;
    }

    public <T> T getValue(Class<T> clazz) {
        return clazz.cast(value);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }
}
