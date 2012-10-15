package net.parostroj.timetable.utils;

/**
 * Class filter.
 *
 * @author jub
 *
 * @param <T>
 */
public class ClassFilter<T> implements Filter<T> {

    private final Class<T> clazz;

    public ClassFilter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean is(Object item) {
        return clazz.isInstance(item);
    }

    @Override
    public T get(Object item) {
        return clazz.cast(item);
    }
}
