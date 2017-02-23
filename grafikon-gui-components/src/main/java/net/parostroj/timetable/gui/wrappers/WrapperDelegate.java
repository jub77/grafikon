package net.parostroj.timetable.gui.wrappers;

import java.util.Comparator;

/**
 * Wrapper delegate.
 *
 * @author jub
 */
public interface WrapperDelegate<T> extends WrapperConversion<T> {

    default String toCompareString(T element) {
        String string = element == null ? "" : toString(element);
        return string == null ? "" : string;
    }

    default Comparator<? super T> getComparator() {
        return null;
    }
}
