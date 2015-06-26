package net.parostroj.timetable.actions;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Sorting of elements.
 *
 * @author jub
 */
public class ElementSort<T> {

    private final Comparator<? super T> comparator;
    private final Predicate<? super T> filter;

    public ElementSort(Comparator<? super T> comparator) {
        this(comparator, null);
    }

    public ElementSort(Comparator<? super T> comparator, Predicate<? super T> filter) {
        this.comparator = comparator;
        this.filter = filter;
    }

    /**
     * sorts list of elements.
     *
     * @param elements elements
     * @return sorted list
     */
    public List<T> sort(Collection<? extends T> elements) {
        List<T> newElements = filter == null ? Lists.newArrayList(elements) : Lists.newArrayList(Iterables.filter(
                elements, filter));
        Collections.sort(newElements, comparator);
        return newElements;
    }

    public static <E> List<E> sort(Collection<? extends E> elements, Comparator<? super E> comparator) {
        return elements.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static <E> List<E> sort(Collection<? extends E> elements, Comparator<? super E> comparator,
            Predicate<? super E> filter) {
        return elements.stream().filter(getPredicate(filter)).sorted(comparator).collect(Collectors.toList());
    }

    public static <E> java.util.function.Predicate<? super E> getPredicate(Predicate<? super E> predicate) {
        return item -> predicate.apply(item);
    }
}
