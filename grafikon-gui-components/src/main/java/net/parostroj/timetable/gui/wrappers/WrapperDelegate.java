package net.parostroj.timetable.gui.wrappers;

/**
 * Wrapper delegate.
 *
 * @author jub
 */
public interface WrapperDelegate<T> extends WrapperConversion<T> {

    public int compare(T o1, T o2);
}
