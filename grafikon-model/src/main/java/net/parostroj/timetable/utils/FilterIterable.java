package net.parostroj.timetable.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Filtering iterable.
 *
 * @author jub
 */
public class FilterIterable<T> implements Iterable<T> {

    private Collection<?> collection;
    private Class<T> clazz;

    public FilterIterable(Collection<?> collection, Class<T> clazz) {
        this.collection = collection;
        this.clazz = clazz;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Iterator<?> iterator = collection.iterator();
            private T next;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    while (iterator.hasNext()) {
                        Object o = iterator.next();
                        if (clazz.isInstance(o)) {
                            next = clazz.cast(o);
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
                    } while (!clazz.isInstance(o));
                    return clazz.cast(o);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }
}
