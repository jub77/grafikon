package net.parostroj.timetable.utils;

/**
 * Filter.
 *
 * @author jub
 *
 * @param <T>
 */
public interface ExtractionFilter<T, U> extends Filter<U> {

    T get(U item);
}
