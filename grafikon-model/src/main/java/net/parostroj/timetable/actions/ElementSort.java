package net.parostroj.timetable.actions;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public <E extends T> List<E> sort(Collection<E> elements) {
        return sort(elements, comparator, filter);
    }

    public static <E> List<E> sort(Collection<E> elements, Comparator<? super E> comparator) {
        return sort(elements, comparator, null);
    }

    public static <E> List<E> sort(Collection<E> elements, Comparator<? super E> comparator,
            Predicate<? super E> filter) {
        Stream<E> stream = elements.stream();
        if (filter != null) {
            stream = stream.filter(filter);
        }
        return stream.sorted(comparator).collect(Collectors.<E>toList());
    }
}
