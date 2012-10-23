package net.parostroj.timetable.utils;

/**
 * Filter interface.
 *
 * @author jub
 */
public interface Filter<U> {

    boolean is(U item);
}
