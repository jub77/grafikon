package net.parostroj.timetable.filters;

import java.util.Collection;
import java.util.Iterator;

/**
 * Filtering iterable.
 *
 * @author jub
 */
public class ExtractionFilterIterable<T, U> implements Iterable<T> {

    private final Collection<U> collection;
    private final ExtractionFilter<T, U> filter;

    public ExtractionFilterIterable(Collection<U> collection, ExtractionFilter<T, U> filter) {
        this.collection = collection;
        this.filter = filter;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private final Iterator<U> iterator = collection.iterator();
            private T next;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    while (iterator.hasNext()) {
                        U o = iterator.next();
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
                    U o = null;
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
