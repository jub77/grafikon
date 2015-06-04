package net.parostroj.timetable.gui.wrappers;

import java.text.Collator;

/**
 * Basic wrapper delegate.
 *
 * @author jub
 */
public class BasicWrapperDelegate<T> implements WrapperDelegate<T> {

    private static final Collator collator = Collator.getInstance();

    protected Collator getCollator() {
        return collator;
    }

    protected String toCompareString(T element) {
        return toString(element);
    }

    @Override
    public String toString(T element) {
        return element.toString();
    }

    @Override
    public int compare(T o1, T o2) {
        return getCollator().compare(toCompareString(o1), (toCompareString(o2)));
    }
}
