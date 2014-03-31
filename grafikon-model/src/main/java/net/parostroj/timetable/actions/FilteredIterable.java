package net.parostroj.timetable.actions;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over time instances and applies filter.
 *
 * @author jub
 */
public class FilteredIterable<T> implements Iterable<T> {

    private final Iterable<T> iterable;
    private final Filter<T> filter;

    public FilteredIterable(Iterable<T> iterable, Filter<T> filter) {
        this.iterable = iterable;
        this.filter = filter;
    }

    public interface Filter<T> {
        boolean is(T instance);
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> i = iterable.iterator();
        return new Iterator<T>() {

            private T next = null;

            @Override
            public boolean hasNext() {
                this.loadNext();
                return next != null;
            }

            @Override
            public T next() {
                loadNext();
                if (next != null) {
                    T returned = next;
                    next = null;
                    return returned;
                } else {
                    throw new NoSuchElementException();
                }
            }

            private void loadNext() {
                while (next == null && i.hasNext()) {
                   T instance = i.next();
                   if (filter.is(instance)) {
                       next = instance;
                   }
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
