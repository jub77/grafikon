package net.parostroj.timetable.gui.wrappers;

/**
 * Basic wrapper delegate.
 *
 * @author jub
 */
public class BasicWrapperDelegate implements WrapperDelegate {

    @Override
    public String toString(Object element) {
        return element.toString();
    }

    @Override
    public int compare(Object o1, Object o2) {
        return toString(o1).compareTo(toString(o2));
    }
}
