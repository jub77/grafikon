package net.parostroj.timetable.gui.wrappers;

import java.text.Collator;

/**
 * Basic wrapper delegate.
 *
 * @author jub
 */
public class BasicWrapperDelegate implements WrapperDelegate<Object> {

    private static final Collator collator = Collator.getInstance();

    protected Collator getCollator() {
        return collator;
    }

    protected String toCompareString(Object element) {
        return toString(element);
    }

    @Override
    public String toString(Object element) {
        return element.toString();
    }

    @Override
    public int compare(Object o1, Object o2) {
        return getCollator().compare(toCompareString(o1), (toCompareString(o2)));
    }
}
