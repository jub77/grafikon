package net.parostroj.timetable.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Filtering iterable.
 *
 * @author jub
 */
public class FilterIterable<T> implements Iterable<T> {

    private final Collection<?> collection;
    private final Filter<T> filter;

    public FilterIterable(Collection<?> collection, Filter<T> filter) {
        this.collection = collection;
        this.filter = filter;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private final Iterator<?> iterator = collection.iterator();
            private T next;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    while (iterator.hasNext()) {
                        Object o = iterator.next();
                        if (filter.is(o)) {
                            next = filter.get(o);
                            break;
                        }
                    }
                }
                return next != null;
            }

            @Override
            public T next() {
                if (next != null) {
                    T result = next;
                    next = null;
                    return result;
                } else {
                    Object o = null;
                    do {
                        o = iterator.next();
                    } while (!filter.is(o));
                    return filter.get(o);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }
}
