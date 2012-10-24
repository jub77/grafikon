package net.parostroj.timetable.filters;

/**
 * Filter interface.
 *
 * @author jub
 */
public interface Filter<U> {

    boolean is(U item);
}
