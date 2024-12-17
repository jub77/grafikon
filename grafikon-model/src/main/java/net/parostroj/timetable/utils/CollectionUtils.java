package net.parostroj.timetable.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.function.Predicate;

/**
 * Utility methods for collections.
 *
 * @author jub
 */
public final class CollectionUtils {

    private CollectionUtils() {}

    public static <T> boolean advanceTo(PeekingIterator<T> iterator, Predicate<T> predicate) {
        boolean found = false;
        while (iterator.hasNext() && !(found = predicate.test(iterator.peek()))) {
            iterator.next();
        }
        return found;
    }

    public static <T> Iterable<T> closedIntervalIterable(Iterable<T> iterable, T start, T end) {
        return () -> closedIntervalIterator(iterable.iterator(), start, end);
    }

    /**
     * Returns iterator which contains only closed interval delimited by the parameters.
     *
     * @param iterator source iterator
     * @param start start element
     * @param end end element
     * @return iterator with interval
     */
    public static <T> Iterator<T> closedIntervalIterator(Iterator<T> iterator, T start, T end) {
        class ClosedIterator extends AbstractIterator<T> {
            boolean in = false;
            boolean stopped = false;

            @Override
            protected T computeNext() {
                if (!in) {
                    T value = Iterators.find(iterator, item -> item == start, null);
                    if (value != null) {
                        in = true;
                        stopped = value == end;
                        return value;
                    }
                }
                if (!stopped && in && iterator.hasNext()) {
                    T value = iterator.next();
                    if (value == end) {
                        stopped = true;
                    }
                    return value;
                }
                return endOfData();
            }
        }
        return new ClosedIterator();
    }

    public static <T> Iterable<T> openIntervalIterable(Iterable<T> iterable, T start, T end) {
        return () -> openIntervalIterator(iterable.iterator(), start, end);
    }

    /**
     * Returns iterator which contains only open interval delimited by the parameters.
     *
     * @param iterator source iterator
     * @param start start element
     * @param end end element
     * @return iterator with interval
     */
    public static <T> Iterator<T> openIntervalIterator(Iterator<T> iterator, T start, T end) {
        class OpenIterator extends AbstractIterator<T> {
            boolean in = false;

            @Override
            protected T computeNext() {
                if (!in) {
                    T value = Iterators.find(iterator, item -> item == start, null);
                    in = value != null && value != end;
                }
                if (in && iterator.hasNext()) {
                    T value = iterator.next();
                    if (value != end) {
                        return value;
                    }
                }
                return endOfData();
            }
        }
        return new OpenIterator();
    }

    /**
     * Returns iterable with sliding window of original iterable.
     *
     * @param iterable list
     * @param count window size
     * @return list with window
     */
    public static <T> Iterable<List<T>> slidingWindow(final Iterable<T> iterable, final int count) {
        return () -> slidingWindow(iterable.iterator(), count);
    }

    /**
     * Returns iterator with sliding window.
     *
     * @param iterator source iterator
     * @param count window size
     * @return iterator with window
     */
    public static <T> Iterator<List<T>> slidingWindow(final Iterator<T> iterator, final int count) {
        return new AbstractIterator<>() {

            private final LinkedList<T> window = new LinkedList<>();
            private int cnt = 0;

            @Override
            protected List<T> computeNext() {
                if (!window.isEmpty()) {
                    window.removeFirst();
                    cnt--;
                }
                if (!iterator.hasNext()) {
                    return endOfData();
                }
                while (iterator.hasNext() && cnt < count) {
                    window.add(iterator.next());
                    cnt++;
                }
                return ImmutableList.copyOf(window);
            }
        };
    }

    /**
     * Returns iterable with tuples from source iterable.
     *
     * @param iterable source iterable
     * @return iterable with tuples
     */
    public static <T> Iterable<Tuple<T>> tuples(final Iterable<T> iterable) {
        return () -> tuples(iterable.iterator());
    }

    /**
     * Returns iterator with tuples from source iterator.
     *
     * @param iterator source iterator
     * @return iterator with tuples
     */
    public static <T> Iterator<Tuple<T>> tuples(final Iterator<T> iterator) {
        return new AbstractIterator<>() {

            @Override
            protected Tuple<T> computeNext() {
                if (iterator.hasNext()) {
                    T first = iterator.next();
                    if (iterator.hasNext()) {
                        T second = iterator.next();
                        return new Tuple<>(first, second);
                    }
                }
                return endOfData();
            }
        };
    }
}
