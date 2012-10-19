package net.parostroj.timetable.utils;

/**
 * Empty filter.
 *
 * @author cz2b10k5
 */
public class EmptyFilter<T> implements Filter<T> {

    private final Class<T> clazz;

    public EmptyFilter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean is(Object item) {
        return true;
    }

    @Override
    public T get(Object item) {
        return clazz.cast(item);
    }
}
