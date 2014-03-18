package net.parostroj.timetable.filters;

import java.util.*;

/**
 * Utility class.
 *
 * @author jub
 */
public class Filters {

    public static <C extends Collection<T>, T> C filter(C collection, Filter<T> filter, C result) {
        for (T item : collection) {
            if (filter.is(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
