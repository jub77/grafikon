package net.parostroj.timetable.gui.wrappers;

import java.util.Comparator;

/**
 * Wrapper delegate.
 *
 * @author jub
 */
public interface WrapperDelegate<T> extends WrapperConversion<T> {

    default String toCompareString(T element) {
        return "";
    }

    default Comparator<? super T> getComparator() {
        return null;
    }
}
