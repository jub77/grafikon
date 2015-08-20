package net.parostroj.timetable.utils;

import java.util.*;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

/**
 * Utility methods for collections.
 *
 * @author jub
 */
public final class CollectionUtils {

    public static <T> boolean advanceTo(PeekingIterator<T> iterator, Predicate<T> predicate) {
        boolean found = false;
        while (iterator.hasNext() && !(found = predicate.apply(iterator.peek()))) {
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
}
