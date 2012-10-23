package net.parostroj.timetable.utils;

/**
 * Class filter.
 *
 * @author jub
 *
 * @param <T>
 */
public class ClassFilter<T, U> implements Filter<T, U> {

    private final Class<T> clazz;

    public ClassFilter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean is(U item) {
        return clazz.isInstance(item);
    }

    @Override
    public T get(U item) {
        return clazz.cast(item);
    }
}
