package net.parostroj.timetable.utils;

/**
 * Filter.
 *
 * @author jub
 *
 * @param <T>
 */
public interface ExtractionFilter<T, U> {

    boolean is(U item);

    T get(U item);
}
