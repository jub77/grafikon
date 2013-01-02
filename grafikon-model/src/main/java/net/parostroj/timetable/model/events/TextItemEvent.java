package net.parostroj.timetable.model.events;

import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.visitors.EventVisitor;

/**
 * Text item event.
 *
 * @author jub
 */
public class TextItemEvent extends GTEvent<TextItem> {

    public TextItemEvent(TextItem textItem, GTEventType type) {
        super(textItem, type);
    }

    public TextItemEvent(TextItem textItem, AttributeChange change) {
        super(textItem, GTEventType.ATTRIBUTE);
        this.setAttributeChange(change);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TextItemEvent[");
        builder.append(getSource()).append(',');
        builder.append(getType());
        if (getType() == GTEventType.ATTRIBUTE) {
            builder.append(',').append(getAttributeChange());
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

}
