package net.parostroj.timetable.gui.wrappers;

/**
 * Wrapper delegate.
 *
 * @author jub
 */
public interface WrapperDelegate {

    public String toString(Object element);

    public int compare(Object o1, Object o2);
}
