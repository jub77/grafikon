package net.parostroj.timetable.utils;

/**
 * Holder for passing parameter by reference.
 *
 * @author jub
 */
public class ReferenceHolder<T> {
    private T reference;

    public ReferenceHolder() {
        reference = null;
    }

    public ReferenceHolder(T reference) {
        this.reference = reference;
    }

    public T get() {
        return this.reference;
    }

    public void set(T reference) {
        this.reference = reference;
    }
}
