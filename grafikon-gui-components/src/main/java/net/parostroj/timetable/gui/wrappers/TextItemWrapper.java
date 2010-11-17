package net.parostroj.timetable.gui.wrappers;

import net.parostroj.timetable.model.TextItem;

/**
 * Wrapper for text item.
 *
 * @author jub
 */
public class TextItemWrapper extends Wrapper<TextItem> {

    public TextItemWrapper(TextItem element) {
        super(element);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return new StringBuilder(getElement().getName()).append(" (").
                append(getElement().getType()).append(')').toString();
    }
}
