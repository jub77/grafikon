package net.parostroj.timetable.actions;

import java.util.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Sorting of elements.
 *
 * @author jub
 */
public class ElementSort<T> {

    public enum Type {
        ASC, DESC;
    }

    private final Type type;
    private final Comparator<? super T> comparator;
    private final Predicate<? super T> filter;

    public ElementSort(Comparator<? super T> comparator) {
        this(Type.ASC, comparator);
    }

    public ElementSort(Type type, Comparator<? super T> comparator) {
        this(type, comparator, null);
    }

    public ElementSort(Comparator<? super T> comparator, Predicate<? super T> filter) {
        this(Type.ASC, comparator, filter);
    }

    public ElementSort(Type type, Comparator<? super T> comparator, Predicate<? super T> filter) {
        this.type = type;
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
        List<T> newElements = filter == null ? Lists.newArrayList(elements) :
            Lists.newArrayList(Iterables.filter(elements, filter));
        Comparator<? super T> lComparator = comparator;
        if (type == Type.DESC) {
            lComparator = lComparator.reversed();
        }
        Collections.sort(newElements, comparator);
        return newElements;
    }
}
