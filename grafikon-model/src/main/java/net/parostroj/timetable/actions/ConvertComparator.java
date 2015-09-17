package net.parostroj.timetable.actions;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Wrapper comparator with transformation.
 *
 * @author jub
 *
 * @param <T>
 * @param <V>
 */
public class ConvertComparator<V, T> implements Comparator<V> {

    private final Comparator<T> comparator;
    private final Function<V, T> transformation;

    public ConvertComparator(Comparator<T> comparator, Function<V, T> transformation) {
        this.comparator = comparator;
        this.transformation = transformation;
    }

    @Override
    public int compare(V o1, V o2) {
        return comparator.compare(transformation.apply(o1), transformation.apply(o2));
    }
}
