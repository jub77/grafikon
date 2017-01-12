package net.parostroj.timetable.gui.wrappers;

import java.text.Collator;

/**
 * Basic wrapper delegate.
 *
 * @author jub
 */
public class BasicWrapperDelegate<T> implements WrapperDelegate<T> {

    protected static final Collator collator = Collator.getInstance();

    protected Collator getCollator() {
        return collator;
    }

    @Override
    public String toCompareString(T element) {
        return toString(element);
    }

    @Override
    public String toString(T element) {
        return element.toString();
    }
}
