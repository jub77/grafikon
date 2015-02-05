package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.AbstractValuePM;

public class ValuePM<T> extends AbstractValuePM {

    private T value;

    public ValuePM(Class<T> clazz) {
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        T old = this.value;
        this.value = value;
        getPropertyChangeSupport().firePropertyChange("value", old, value);
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }
}
