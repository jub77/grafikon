package net.parostroj.timetable.utils;

/**
 * Filter.
 *
 * @author jub
 *
 * @param <T>
 */
public interface Filter<T, U> {

    boolean is(U item);

    T get(U item);
}
