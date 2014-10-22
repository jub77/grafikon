package net.parostroj.timetable.output2.gt;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class SelectorUtils {

    public static <T> T select(Iterable<? extends T> list, T old, Predicate<? super T> filter) {
        Iterable<? extends T> filteredList = Iterables.filter(list, filter);
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
}
