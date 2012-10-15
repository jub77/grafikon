package net.parostroj.timetable.utils;

/**
 * Filter.
 *
 * @author jub
 *
 * @param <T>
 */
public interface Filter<T> {

    boolean is(Object item);

    T get(Object item);
}
