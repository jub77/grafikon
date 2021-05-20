package net.parostroj.timetable.output2.gt;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.util.function.Function;
import java.util.function.Predicate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

import com.google.common.collect.Iterables;

public final class SelectorUtils {

    private SelectorUtils() {}

    public static <T> T select(Iterable<? extends T> list, T old, Predicate<? super T> filter) {
        Iterable<? extends T> filteredList = Iterables.filter(list, filter::test);
        return select(filteredList, old);
    }

    public static <T> T select(Iterable<? extends T> list, T old) {
        Iterator<? extends T> i = list.iterator();
        if (!i.hasNext()) {
            return null;
        } else {
            if (old == null) {
                return i.next();
            } else {
                T item = i.next();
                T first = item;
                while (i.hasNext() && old != item) {
                    item = i.next();
                }
                return i.hasNext() ? i.next() : first;
            }
        }
    }

    public static Predicate<TimeInterval> createUniqueTrainIntervalFilter() {
        final List<Train> collected = new LinkedList<>();
        return interval -> {
            if (!collected.contains(interval.getTrain())) {
                collected.add(interval.getTrain());
                return true;
            } else {
                return false;
            }
        };
    }

    public static Predicate<Train> createUniqueTrainFilter() {
        final List<Train> collected = new LinkedList<>();
        return input -> {
            if (!collected.contains(input)) {
                collected.add(input);
                return true;
            } else {
                return false;
            }
        };
    }

    public static Function<TimeInterval, Train> createToTrainFunction() {
        return TimeInterval::getTrain;
    }
}
