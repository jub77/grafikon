package net.parostroj.timetable.gui.helpers;

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
    public String toString() {
        return new StringBuilder(getElement().getName()).append(" (").
                append(getElement().getType()).append(')').toString();
    }
}
